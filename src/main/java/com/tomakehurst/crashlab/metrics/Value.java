package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class Value<T> {

    private final T value;

    @JsonCreator
    public Value(@JsonProperty("value") T value) {
        this.value = value;
    }

    @JsonValue
    public T getValue() {
        return value;
    }
}
