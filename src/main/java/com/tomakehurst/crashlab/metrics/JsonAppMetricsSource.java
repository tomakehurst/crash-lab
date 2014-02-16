package com.tomakehurst.crashlab.metrics;

public abstract class JsonAppMetricsSource {

    protected abstract String getJson();

    public AppMetrics fetch() {
        return null;
    }
}
