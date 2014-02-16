package com.tomakehurst.crashlab;

import java.util.concurrent.TimeUnit;

public class TimeInterval {

    private final double time;
    private final TimeUnit timeUnit;

    public TimeInterval(double time, TimeUnit timeUnit) {
        this.time = time;
        this.timeUnit = timeUnit;
    }

    public static TimeInterval interval(double time, TimeUnit timeUnit) {
        return null;
    }

    public static TimeInterval period(double time, TimeUnit timeUnit) {
        return interval(time, timeUnit);
    }

    public double time() {
        return time;
    }

    public TimeUnit timeUnit() {
        return timeUnit;
    }
}
