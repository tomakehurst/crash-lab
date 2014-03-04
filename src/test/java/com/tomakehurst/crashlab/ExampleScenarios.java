package com.tomakehurst.crashlab;

import com.ning.http.client.*;
import com.tomakehurst.crashlab.breakbox.BreakBox;
import com.tomakehurst.crashlab.breakbox.Fault;
import com.tomakehurst.crashlab.metrics.AppMetrics;
import com.tomakehurst.crashlab.metrics.HttpJsonAppMetricsSource;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.tomakehurst.crashlab.Rate.rate;
import static com.tomakehurst.crashlab.TimeInterval.interval;
import static com.tomakehurst.crashlab.TimeInterval.period;
import static com.tomakehurst.crashlab.breakbox.Delay.delay;
import static com.tomakehurst.crashlab.breakbox.FirewallTimeout.firewallTimeout;
import static com.tomakehurst.crashlab.breakbox.PacketLoss.packetLoss;
import static com.tomakehurst.crashlab.breakbox.ServiceFailure.serviceFailure;
import static com.tomakehurst.crashlab.metrics.TimeIntervalMatchers.lessThan;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExampleScenarios {

    static final String BASE_URL = "http://192.168.2.12";
    static final String GET_TEXT_URL = BASE_URL + ":8080/no-connect-timeout";
    static final String GET_TEXT_WITH_BAD_ERROR_HANDLING_URL = BASE_URL + ":8080/bad-http-client-error-handling";

    static HttpJsonAppMetricsSource metricsSource = new HttpJsonAppMetricsSource(BASE_URL + ":8081/metrics");
    static CrashLab crashLab = new CrashLab();
    BreakBox textSnippetServiceBreakBox = BreakBox.defineClient("text-snippet-web-service", 8080, "192.168.2.12");


    @Before
    public void init() {
        textSnippetServiceBreakBox.reset();
    }

    @Test
    public void latency_less_than_200ms_with_no_faults() {
        crashLab.run(period(10, SECONDS), rate(20).per(SECONDS), new HttpSteps("10 seconds moderate load") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_URL).execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.no-connect-timeout.timer").percentile95();
        assertTrue("Expected 95th percentile latency to be less than 200 milliseconds. Was actually " + p95.timeIn(MILLISECONDS) + "ms",
                p95.lessThan(interval(200, MILLISECONDS)));
    }

    @Test
    public void latency_should_not_exceed_1s_when_network_flaky() {
        textSnippetServiceBreakBox.addFault(packetLoss("flaky-connection-to-text").probability(40).correlation(15));

        crashLab.run(period(10, SECONDS), rate(50).per(SECONDS), new HttpSteps("GET web resource repeatedly") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_URL).execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.no-connect-timeout.timer").percentile95();
        assertThat(p95, lessThan(1000, MILLISECONDS));
    }

    @Test
    public void latency_should_not_exceed_1s_when_network_slow() {
        textSnippetServiceBreakBox.addFault(delay("text-service-slowness").delay(1, SECONDS).variance(500, MILLISECONDS));

        crashLab.run(period(10, SECONDS), rate(30).per(SECONDS), new HttpSteps("GET web resource repeatedly") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_URL).execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.no-connect-timeout.timer").percentile95();
        assertThat(p95, lessThan(1000, MILLISECONDS));
    }

    @Test(timeout = 20000)
    public void should_not_lock_up_after_pooled_connections_to_text_service_have_timed_out() throws Exception {
        printHttpClientPoolGauges();

        textSnippetServiceBreakBox.addFault(firewallTimeout("text-service-firewall-timeouts").timeout(5, SECONDS));
        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), new HttpSteps("Warm-up") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_URL).execute(completionHandler);
            }
        });

        sleepUninterruptibly(6, SECONDS); // 1s longer than TCP timeout

        AsyncHttpClient httpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setRequestTimeoutInMs(1000).build());
        Response response = httpClient.prepareGet(GET_TEXT_URL).execute().get();
        assertThat(response.getStatusCode(), is(200));

        printHttpClientPoolGauges();
    }

    @Test
    public void should_not_leak_pooled_http_connections_when_text_service_responses_sometimes_time_out() throws Exception {
        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), new HttpSteps("Warm-up") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_WITH_BAD_ERROR_HANDLING_URL).execute(completionHandler);
            }
        });

        int initialLeasedConnections = metricsSource.fetch()
                .gauge("org.apache.http.conn.ClientConnectionManager.wiremock-client.leased-connections");

        textSnippetServiceBreakBox.addFault(delay("text-service-delay").delay(500, MILLISECONDS).variance(250, MILLISECONDS));

        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), new HttpSteps("Run with broken text service") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_WITH_BAD_ERROR_HANDLING_URL).execute(completionHandler);
            }
        });

        int finalLeasedConnections = metricsSource.fetch()
                .gauge("org.apache.http.conn.ClientConnectionManager.wiremock-client.leased-connections");
        assertThat(finalLeasedConnections, lessThanOrEqualTo(plus50Percent(initialLeasedConnections)));
    }

    private int plus50Percent(int value) {
        return (int) (value * 1.5);
    }

    private void printHttpClientPoolGauges() {
        AppMetrics appMetrics = metricsSource.fetch();
        Integer availableConnections = appMetrics.gauge("org.apache.http.conn.ClientConnectionManager.wiremock-client.available-connections");
        Integer leasedConnections = appMetrics.gauge("org.apache.http.conn.ClientConnectionManager.wiremock-client.leased-connections");
        System.out.printf("Available connections: %d, leased connections: %d\n", availableConnections, leasedConnections);
    }

}
