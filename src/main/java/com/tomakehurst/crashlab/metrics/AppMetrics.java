package com.tomakehurst.crashlab.metrics;

import com.fasterxml.
import java.net.URI;
import java.util.List;

public class AppMetrics {

    private final List<TimerSnapshot> timers;

    public AppMetrics(@JsonProperty List<TimerSnapshot> timers) {
        this.timers = timers;
    }

    public TimerSnapshot timer(String name) {
        return null;
    }
}
