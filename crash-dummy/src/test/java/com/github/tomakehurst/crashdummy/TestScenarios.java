package com.github.tomakehurst.crashdummy;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.ning.http.client.*;
import com.tomakehurst.crashlab.CrashLab;
import com.tomakehurst.crashlab.HttpSteps;
import com.tomakehurst.crashlab.TimeInterval;
import com.tomakehurst.crashlab.metrics.AppMetrics;
import com.tomakehurst.crashlab.metrics.HttpJsonAppMetricsSource;
import com.tomakehurst.crashlab.saboteur.Saboteur;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static com.tomakehurst.crashlab.Rate.rate;
import static com.tomakehurst.crashlab.TimeInterval.interval;
import static com.tomakehurst.crashlab.TimeInterval.period;
import static com.tomakehurst.crashlab.metrics.TimeIntervalMatchers.lessThan;
import static com.tomakehurst.crashlab.saboteur.Delay.delay;
import static com.tomakehurst.crashlab.saboteur.FirewallTimeout.firewallTimeout;
import static com.tomakehurst.crashlab.saboteur.PacketLoss.packetLoss;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TestScenarios {

    static final String BASE_URL = "http://192.168.2.12";
    static final String GET_TEXT_URL = BASE_URL + ":8080/some-text";
    static final String GET_TEXT_WITH_BAD_ERROR_HANDLING_URL = BASE_URL + ":8080/bad-http-client-error-handling";
    private static final String SHORT_TIMEOUT_CLIENT_GAGUES = "org.apache.http.conn.ClientConnectionManager.wiremock-short-timeout-client";

    static HttpJsonAppMetricsSource metricsSource = new HttpJsonAppMetricsSource(BASE_URL + ":8081/metrics");
    static CrashLab crashLab = new CrashLab();

    Saboteur textSnippetServiceSaboteur = Saboteur.defineService("text-snippet-web-service", 8080, "192.168.2.11");
    WireMock textSnippetService = new WireMock("192.168.2.11", 8080);

    AsyncHttpClient http = new AsyncHttpClient();

    @Before
    public void init() {
        textSnippetServiceSaboteur.reset();
        textSnippetService.resetToDefaultMappings();
    }

    @Test
    public void should_fail_with_503_response_when_text_snippet_service_unavailable() {

    }

    @Test
    public void latency_less_than_200ms_with_no_faults() {
        crashLab.run(period(10, SECONDS), rate(20).per(SECONDS), new HttpSteps("10 seconds moderate load") {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(GET_TEXT_URL).execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.some-text.timer").percentile95();
        assertTrue("Expected 95th percentile latency to be less than 200 milliseconds. Was actually " + p95.timeIn(MILLISECONDS) + "ms",
                p95.lessThan(interval(200, MILLISECONDS)));
    }

    @Test
    public void latency_should_not_exceed_1s_when_network_flaky() {
        textSnippetServiceSaboteur.addFault(packetLoss("flaky-connection-to-text").probability(40).correlation(15));

        crashLab.run(period(10, SECONDS), rate(50).per(SECONDS), httpGet(GET_TEXT_URL, "GET web resource repeatedly"));

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.some-text.timer").percentile95();
        assertThat(p95, lessThan(1000, MILLISECONDS));
    }

    @Test
    public void latency_should_not_exceed_1s_when_network_slow() {
        textSnippetServiceSaboteur.addFault(delay("text-service-slowness").delay(1, SECONDS).variance(500, MILLISECONDS));

        crashLab.run(period(10, SECONDS), rate(30).per(SECONDS), httpGet(GET_TEXT_URL, "GET web resource repeatedly"));

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.some-text.timer").percentile95();
        assertThat(p95, lessThan(1000, MILLISECONDS));
    }

    @Test(timeout = 20000)
    public void should_not_lock_up_after_pooled_connections_to_text_service_have_timed_out() throws Exception {
        printHttpClientPoolGauges("org.apache.http.conn.ClientConnectionManager.wiremock-client");

        textSnippetServiceSaboteur.addFault(firewallTimeout("text-service-firewall-timeouts").timeout(5, SECONDS));
        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), httpGet(GET_TEXT_URL, "Warm up"));

        sleepUninterruptibly(6, SECONDS); // 1s longer than TCP timeout

        AsyncHttpClient httpClient = new AsyncHttpClient(new AsyncHttpClientConfig.Builder().setRequestTimeoutInMs(1000).build());
        Response response = httpClient.prepareGet(GET_TEXT_URL).execute().get();
        assertThat(response.getStatusCode(), is(200));

        printHttpClientPoolGauges("org.apache.http.conn.ClientConnectionManager.wiremock-client");
    }

    @Test
    public void should_not_leak_pooled_http_connections_when_text_service_returns_503_responses() throws Exception {
        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), httpGet(GET_TEXT_WITH_BAD_ERROR_HANDLING_URL, "Warm-up"));

        printHttpClientPoolGauges(SHORT_TIMEOUT_CLIENT_GAGUES);
        int initialLeasedConnections = metricsSource.fetch()
                .gauge(SHORT_TIMEOUT_CLIENT_GAGUES + ".leased-connections");

        textSnippetService.register(get(urlEqualTo("/something")).willReturn(aResponse().withStatus(503)));

        crashLab.run(period(5, SECONDS), rate(30).per(SECONDS), httpGet(GET_TEXT_WITH_BAD_ERROR_HANDLING_URL, "Run with broken text service"));

        printHttpClientPoolGauges(SHORT_TIMEOUT_CLIENT_GAGUES);
        int finalLeasedConnections = metricsSource.fetch()
                .gauge(SHORT_TIMEOUT_CLIENT_GAGUES + ".leased-connections");
        assertThat(finalLeasedConnections, lessThanOrEqualTo(plus50Percent(initialLeasedConnections)));
    }

    private HttpSteps httpGet(final String url, final String name) {
        return new HttpSteps(name) {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                return http.prepareGet(url).execute(completionHandler);
            }
        };
    }

    private int plus50Percent(int value) {
        return (int) (value * 1.5);
    }

    private void printHttpClientPoolGauges(String client) {
        AppMetrics appMetrics = metricsSource.fetch();
        Integer availableConnections = appMetrics.gauge(client + ".available-connections");
        Integer leasedConnections = appMetrics.gauge(client + ".leased-connections");
        System.err.printf("Available connections: %d, leased connections: %d\n", availableConnections, leasedConnections);
    }

}
