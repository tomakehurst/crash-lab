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

    static HttpJsonAppMetricsSource metricsSource = new HttpJsonAppMetricsSource("http://localhost:8081/metrics");
    static CrashLab crashLab = new CrashLab();

    @Test
    public void apply_load_and_assert_metrics_with_no_faults() {
        crashLab.run(period(10, SECONDS), rate(20).per(SECONDS), new HttpSteps() {
            protected String name() {
                return "Example steps";
            }

            public void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                http.prepareGet("http://localhost:8080/no-connect-timeout").execute(completionHandler);
            }
        });

        AppMetrics appMetrics = metricsSource.fetch();
        TimeInterval p95 = appMetrics.timer("webresources.no-connect-timeout.timer").percentile95();
        assertTrue("Expected 95th percentile latency to be less than 200 milliseconds. Was actually " + p95.timeIn(MILLISECONDS) + "ms",
                p95.lessThan(interval(200, MILLISECONDS)));
    }

}
