package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HistogramSnapshot {

    private final long count;
    private final long max;
    private final long min;
    private final double mean;
    private final double p50;
    private final double p75;
    private final double p95;
    private final double p98;
    private final double p99;
    private final double p999;
    private final double stddev;

    public HistogramSnapshot(@JsonProperty("count") long count,
                             @JsonProperty("max") long max,
                             @JsonProperty("min") long min,
                             @JsonProperty("mean") double mean,
                             @JsonProperty("p50") double p50,
                             @JsonProperty("p75") double p75,
                             @JsonProperty("p95") double p95,
                             @JsonProperty("p98") double p98,
                             @JsonProperty("p99") double p99,
                             @JsonProperty("p999") double p999,
                             @JsonProperty("stddev") double stddev) {
        this.count = count;
        this.max = max;
        this.mean = mean;
        this.min = min;
        this.p50 = p50;
        this.p75 = p75;
        this.p95 = p95;
        this.p98 = p98;
        this.p99 = p99;
        this.p999 = p999;
        this.stddev = stddev;
    }

    public long count() {
        return count;
    }

    public long max() {
        return max;
    }

    public long min() {
        return min;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double median() {
        return p50;
    }

    public double percentile75() {
        return p75;
    }

    public double percentile95() {
        return p95;
    }

    public double percentile98() {
        return p98;
    }

    public double percentile99() {
        return p99;
    }

    public double percentile999() {
        return p999;
    }
}
