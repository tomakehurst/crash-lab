package com.tomakehurst.crashlab;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Rate {

    private long quantity;
    private TimeUnit timeUnit;

    public Rate(long quantity) {
        this.quantity = quantity;
    }

    public static Rate rate(long rate) {
        return new Rate(rate);
    }

    public Rate per(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public long periodIn(TimeUnit requiredTimeUnit) {
        double period = 1d / quantity;
        long periodNanos = (long) (period * timeUnit.toNanos(1));
        return requiredTimeUnit.convert(periodNanos, NANOSECONDS);
    }

    public long quantity() {
        return quantity;
    }

    public TimeUnit unit() {
        return timeUnit;
    }
}
