package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.tomakehurst.crashlab.TimeInterval;

import java.util.concurrent.TimeUnit;

import static com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion.NON_NULL;
import static com.tomakehurst.crashlab.TimeInterval.interval;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@JsonSerialize(include = NON_NULL)
public class Delay extends Fault {

    public enum Distribution { UNIFORM, NORMAL, PARETO, PARETONORMAL }

    private TimeInterval delay;
    private TimeInterval variance;
    private Distribution distribution;
    private Integer correlation;

    public static Delay delay(String name) {
        Delay delay = new Delay();
        delay.setName(name);
        return delay;
    }

    @Override
    public Type getType() {
        return Type.DELAY;
    }

    public Delay delay(long time, TimeUnit timeUnit) {
        this.delay = interval(time, timeUnit);
        return this;
    }

    public Delay variance(long time, TimeUnit timeUnit) {
        this.variance = interval(time, timeUnit);
        return this;
    }

    public Delay distribution(Distribution distribution) {
        this.distribution = distribution;
        return this;
    }

    public Delay correlation(int correlation) {
        this.correlation = correlation;
        return this;
    }

    public Long getDelay() {
        return delay == null ? null : delay.timeIn(MILLISECONDS);
    }

    public Long getVariance() {
        return variance == null ? null : variance.timeIn(MILLISECONDS);
    }

    public String getDistribution() {
        return distribution == null ? null : distribution.toString().toLowerCase();
    }

    public Integer getCorrelation() {
        return correlation;
    }

}
