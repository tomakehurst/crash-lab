package com.tomakehurst.crashlab.metrics;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static com.google.common.base.Charsets.UTF_8;
import static java.util.concurrent.TimeUnit.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class AppMetricsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_return_correct_values_for_timer_snapshot() {
        AppMetrics appMetrics = new MockJsonAppMetricsSource().fetch();

        TimerSnapshot timerSnapshot = appMetrics.timer("webresources.no-connect-timeout.timer");
        assertThat(timerSnapshot.count(), is(6L));
        assertThat(timerSnapshot.max().timeIn(MILLISECONDS), is(112L));
        assertThat(timerSnapshot.min().timeIn(MILLISECONDS), is(103L));
        assertThat(timerSnapshot.mean().timeIn(MILLISECONDS), is(105L));
        assertThat(timerSnapshot.stddev().timeIn(MICROSECONDS), is(3463L));
        assertThat(timerSnapshot.median().timeIn(MILLISECONDS), is(104L));
        assertThat(timerSnapshot.percentile75().timeIn(MICROSECONDS), is(107589L));
        assertThat(timerSnapshot.percentile95().timeIn(MICROSECONDS), is(112847L));
        assertThat(timerSnapshot.percentile98().timeIn(MICROSECONDS), is(112848L));
        assertThat(timerSnapshot.percentile99().timeIn(MICROSECONDS), is(112849L));
        assertThat(timerSnapshot.percentile999().timeIn(NANOSECONDS), is(112850000L));
    }

    @Test
    public void throws_useful_exception_when_timer_not_found() {
        expectedException.expectMessage("No timer found named does.not.exist");
        new MockJsonAppMetricsSource().fetch().timer("does.not.exist");
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
