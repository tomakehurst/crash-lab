package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.tomakehurst.crashlab.metrics.AppMetrics;
import com.tomakehurst.crashlab.metrics.HttpJsonAppMetricsSource;
import org.junit.Test;

import java.io.IOException;

import static com.tomakehurst.crashlab.Rate.rate;
import static com.tomakehurst.crashlab.TimeInterval.interval;
import static com.tomakehurst.crashlab.TimeInterval.period;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

public class ExampleScenarioTest {

    static final String BASE_URL = "http://192.168.2.12";

    static HttpJsonAppMetricsSource metricsSource = new HttpJsonAppMetricsSource(BASE_URL + ":8081/metrics");
    static CrashLab crashLab = new CrashLab();
//    BreakBoxClient textSnippetWebService = BreakBox.defineClient("text-snippet-web-service", 8080, "192.168.2.12");

    @Test
    public void apply_load_and_assert_metrics_with_no_faults() {
        crashLab.run(period(10, SECONDS), rate(50).per(SECONDS), new HttpSteps("Example steps") {
            public void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                http.prepareGet(BASE_URL + ":8080/no-connect-timeout").execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.no-connect-timeout.timer").percentile95();
        assertTrue("Expected 95th percentile latency to be less than 200 milliseconds. Was actually " + p95.timeIn(MILLISECONDS) + "ms",
                p95.lessThan(interval(200, MILLISECONDS)));
    }

    @Test
    public void inject_network_flakiness_and_ensure_95th_percentile_capped_at_500ms() {

    }

}
