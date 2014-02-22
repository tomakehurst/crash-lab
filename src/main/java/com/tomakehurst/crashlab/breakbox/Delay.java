package com.tomakehurst.crashlab.breakbox;

import com.tomakehurst.crashlab.TimeInterval;

import java.util.concurrent.TimeUnit;

import static com.tomakehurst.crashlab.TimeInterval.interval;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Delay extends Fault {

    public enum Distribution { UNIFORM, NORMAL, PARETO, PARETONORMAL }

    private TimeInterval delay;
    private TimeInterval variance;
    private Distribution distribution;
    private int correlation;

    Delay(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        super(adminClient, name, direction, protocol, toPort);
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

    public long getDelay() {
        return delay.timeIn(MILLISECONDS);
    }

    public long getVariance() {
        return variance.timeIn(MILLISECONDS);
    }

    public String getDistribution() {
        return distribution.toString().toLowerCase();
    }

    public int getCorrelation() {
        return correlation;
    }
}
