package com.tomakehurst.crashlab;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class TimeInterval {

    private final long nanos;

    public TimeInterval(double time, TimeUnit timeUnit) {
        this.nanos = (long) (timeUnit.toNanos(1) * time);
    }

    public static TimeInterval interval(double time, TimeUnit timeUnit) {
        return new TimeInterval(time, timeUnit);
    }

    public static TimeInterval period(double time, TimeUnit timeUnit) {
        return interval(time, timeUnit);
    }

    public long timeIn(TimeUnit timeUnit) {
        return timeUnit.convert(nanos, NANOSECONDS);
    }

    public long nanos() {
        return nanos;
    }

    public boolean lessThan(TimeInterval other) {
        return this.nanos() < other.nanos();
    }
}
