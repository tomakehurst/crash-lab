package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppMetrics {

    private final Map<String, TimerSnapshot> timers;

    @JsonCreator
    public AppMetrics(@JsonProperty("timers") Map<String, TimerSnapshot> timers) {
        this.timers = timers;
    }

    public TimerSnapshot timer(String name) {
        checkArgument(timers.containsKey(name), "No timer found named " + name);
        return timers.get(name);
    }
}
