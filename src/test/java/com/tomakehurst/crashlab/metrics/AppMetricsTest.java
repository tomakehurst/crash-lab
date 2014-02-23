package com.tomakehurst.crashlab.metrics;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.junit.Before;
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

    AppMetrics appMetrics;

    @Before
    public void init() {
        appMetrics = new StubJsonAppMetricsSource().fetch();
    }

    @Test
    public void should_return_correct_values_for_timer_snapshot() {
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
    public void should_return_correct_value_for_gague() {
        Integer gaugeValue = appMetrics.gauge("org.eclipse.jetty.util.thread.QueuedThreadPool.dw.size");
        assertThat(gaugeValue, is(8));
    }

    @Test
    public void should_return_correct_value_for_meter() {
        MeterSnapshot meterSnapshot = appMetrics.meter("io.dropwizard.jetty.MutableServletContextHandler.2xx-responses");
        assertThat(meterSnapshot.count(), is(5L));
        assertThat(meterSnapshot.m1Rate(), is(0.005540151995103271));
        assertThat(meterSnapshot.m5Rate(), is(0.01652854617838251));
        assertThat(meterSnapshot.m15Rate(), is(0.07995558537067671));
        assertThat(meterSnapshot.meanRate(), is(0.04234514072015045));
    }

    @Test
    public void should_return_correct_value_for_counter() {
        Long counterValue = appMetrics.counter("io.dropwizard.jetty.MutableServletContextHandler.active-dispatches");
        assertThat(counterValue, is(3L));
    }

    @Test
    public void should_return_correct_values_for_histogram() {
        HistogramSnapshot histogramSnapshot = appMetrics.histogram("example-histogram");
        assertThat(histogramSnapshot.count(), is(213L));
        assertThat(histogramSnapshot.max(), is(5L));
        assertThat(histogramSnapshot.min(), is(1L));
        assertThat(histogramSnapshot.mean(), is(3d));
        assertThat(histogramSnapshot.stddev(), is(1.5811388300841898));
        assertThat(histogramSnapshot.median(), is(3d));
        assertThat(histogramSnapshot.percentile75(), is(4.5));
        assertThat(histogramSnapshot.percentile95(), is(5d));
        assertThat(histogramSnapshot.percentile98(), is(5d));
        assertThat(histogramSnapshot.percentile99(), is(5d));
        assertThat(histogramSnapshot.percentile999(), is(5d));
    }

    @Test
    public void throws_useful_exception_when_timer_not_found() {
        expectedException.expectMessage("No timer found named does.not.exist");
        new StubJsonAppMetricsSource().fetch().timer("does.not.exist");
    }

    static class StubJsonAppMetricsSource extends JsonAppMetricsSource {

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
