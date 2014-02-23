package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeterSnapshot {

    private final long count;
    private final double m1Rate;
    private final double m5Rate;
    private final double m15Rate;
    private final double meanRate;
    private final String units;

    public MeterSnapshot(@JsonProperty("count") long count,
                         @JsonProperty("m1_rate") double m1Rate,
                         @JsonProperty("m5_rate") double m5Rate,
                         @JsonProperty("m15_rate") double m15Rate,
                         @JsonProperty("mean_rate") double meanRate,
                         @JsonProperty("units") String units) {
        this.count = count;
        this.m1Rate = m1Rate;
        this.m5Rate = m5Rate;
        this.m15Rate = m15Rate;
        this.meanRate = meanRate;
        this.units = units;
    }

    public long count() {
        return count;
    }

    public double m1Rate() {
        return m1Rate;
    }

    public double m5Rate() {
        return m5Rate;
    }

    public double m15Rate() {
        return m15Rate;
    }

    public double meanRate() {
        return meanRate;
    }

    public String units() {
        return units;
    }
}
