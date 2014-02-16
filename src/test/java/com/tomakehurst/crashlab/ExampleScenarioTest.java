package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.tomakehurst.crashlab.metrics.AppMetrics;
import com.tomakehurst.crashlab.metrics.MetricMatchers;
import org.junit.Test;

import static com.tomakehurst.crashlab.Rate.rate;
import static com.tomakehurst.crashlab.TimeInterval.period;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertThat;

public class ExampleScenarioTest {

    AppMetrics appMetrics = new AppMetrics("http://localhost:8081/metrics");
    CrashLab crashLab = new CrashLab();

    @Test
    public void apply_load_and_assert_metrics_with_no_faults() {
        crashLab.run(period(10, SECONDS), rate(20).per(SECONDS), new HttpSteps() {
            public void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws Exception {
                http.prepareGet("http://localhost:8080/no-connect-timeout").execute(completionHandler);
            }
        });

        assertThat(appMetrics.timer("webresources.no-connect-timeout.timer").percentile99(), MetricMatchers.lessThan(200, MILLISECONDS));
    }

}
