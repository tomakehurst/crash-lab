package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppMetrics {

    private final Map<String, TimerSnapshot> timers;
    private final Map<String, Value<?>> gauges;
    private final Map<String, MeterSnapshot> meters;
    private final Map<String, Counter> counters;
    private final Map<String, HistogramSnapshot> histograms;

    @JsonCreator
    public AppMetrics(@JsonProperty("timers") Map<String, TimerSnapshot> timers,
                      @JsonProperty("gauges") Map<String, Value<?>> gauges,
                      @JsonProperty("meters") Map<String, MeterSnapshot> meters,
                      @JsonProperty("counters") Map<String, Counter> counters,
                      @JsonProperty("histograms") Map<String, HistogramSnapshot> histograms) {
        this.timers = timers;
        this.gauges = gauges;
        this.meters = meters;
        this.counters = counters;
        this.histograms = histograms;
    }

    public TimerSnapshot timer(String name) {
        checkArgument(timers.containsKey(name), "No timer found named " + name);
        return timers.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T gauge(String name) {
        checkArgument(gauges.containsKey(name), "No gauge found named " + name);
        return (T) gauges.get(name).getValue();
    }

    public MeterSnapshot meter(String name) {
        checkArgument(meters.containsKey(name), "No meter found named " + name);
        return meters.get(name);
    }

    public Long counter(String name) {
        checkArgument(counters.containsKey(name), "No counter found named " + name);
        return counters.get(name).value();
    }

    public HistogramSnapshot histogram(String name) {
        checkArgument(histograms.containsKey(name), "No histogram found named " + name);
        return histograms.get(name);
    }
}
