package com.tomakehurst.crashlab.metrics;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.net.URI;

import static java.util.concurrent.TimeUnit.SECONDS;

public class HttpJsonAppMetricsSource extends JsonAppMetricsSource {

    private final URI metricsJsonUrl;
    private final AsyncHttpClient httpClient;

    public HttpJsonAppMetricsSource(URI metricsJsonUrl) {
        this.metricsJsonUrl = metricsJsonUrl;
        this.httpClient = new AsyncHttpClient();
    }

    public HttpJsonAppMetricsSource(String metricsJsonUrl) {
        this(URI.create(metricsJsonUrl));
    }

    @Override
    protected String getJson() {
        try {
            Response response = httpClient.prepareGet(metricsJsonUrl.toString()).execute().get(10, SECONDS);
            return response.getResponseBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
