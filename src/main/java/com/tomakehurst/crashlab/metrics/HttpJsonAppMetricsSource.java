package com.tomakehurst.crashlab.metrics;

import java.net.URI;

public class HttpJsonAppMetricsSource extends JsonAppMetricsSource {

    private final URI metricsJsonUrl;

    public HttpJsonAppMetricsSource(URI metricsJsonUrl) {
        this.metricsJsonUrl = metricsJsonUrl;
    }

    public HttpJsonAppMetricsSource(String metricsJsonUrl) {
        this(URI.create(metricsJsonUrl));
    }

    @Override
    protected String getJson() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public AppMetrics fetch() {
        return null;
    }
}
