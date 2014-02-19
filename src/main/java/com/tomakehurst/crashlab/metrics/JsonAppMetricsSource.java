package com.tomakehurst.crashlab.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public abstract class JsonAppMetricsSource {

    private final ObjectMapper mapper = new ObjectMapper();

    protected abstract String getJson();

    public AppMetrics fetch() {
        try {
            return mapper.readValue(getJson(), AppMetrics.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
