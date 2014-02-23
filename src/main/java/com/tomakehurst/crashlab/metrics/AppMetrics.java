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

    @JsonCreator
    public AppMetrics(@JsonProperty("timers") Map<String, TimerSnapshot> timers,
                      @JsonProperty("gauges") Map<String, Value<?>> gauges) {
        this.timers = timers;
        this.gauges = gauges;
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
}
