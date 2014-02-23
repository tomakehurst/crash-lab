package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Counter {

    private final long count;

    @JsonCreator
    public Counter(@JsonProperty("count") long count) {
        this.count = count;
    }

    @JsonValue
    public long value() {
        return count;
    }
}
