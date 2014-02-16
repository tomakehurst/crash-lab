package com.tomakehurst.crashlab;

import java.util.concurrent.TimeUnit;

public class Rate {

    private long rate;
    private TimeUnit timeUnit;

    public Rate(long rate) {
        this.rate = rate;
    }

    public static Rate rate(long rate) {
        return new Rate(rate);
    }

    public Rate per(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }
}
