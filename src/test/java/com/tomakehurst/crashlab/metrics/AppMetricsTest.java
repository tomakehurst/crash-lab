package com.tomakehurst.crashlab.metrics;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.File;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AppMetricsTest {

    @Test
    public void can_extract_timer_snapshot_99th_percentile() {
        AppMetrics appMetrics = new MockJsonAppMetricsSource().fetch();

        TimerSnapshot timerSnapshot = appMetrics.timer("webresources.no-connect-timeout.timer");
        assertThat(timerSnapshot.percentile99().time(), is(0.112848));
        assertThat(timerSnapshot.percentile99().timeUnit(), is(SECONDS));
    }

    static class MockJsonAppMetricsSource extends JsonAppMetricsSource {

        @Override
        protected String getJson() {
            try {
                return Files.toString(new File(Resources.getResource("metrics-example.json").toURI()), UTF_8);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
