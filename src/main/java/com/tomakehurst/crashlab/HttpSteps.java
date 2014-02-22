package com.tomakehurst.crashlab;

import com.ning.http.client.*;

import java.io.IOException;

public abstract class HttpSteps implements Runnable {

    private final AsyncCompletionHandler<Response> completionHandler;
    private final AsyncHttpClient httpClient;
    private final String name;

    protected HttpSteps(String name) {
        this(name, new NoOpCompetionHandler(), buildClient());
    }

    protected static AsyncHttpClient buildClient() {
        final int timeoutMs = 60 * 60 * 1000;
        AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
                .setConnectionTimeoutInMs(timeoutMs)
                .setRequestTimeoutInMs(timeoutMs)
                .build();
        return new AsyncHttpClient(config);
    }

    protected HttpSteps(String name, AsyncCompletionHandler<Response> completionHandler, AsyncHttpClient httpClient) {
        this.completionHandler = completionHandler;
        this.httpClient = httpClient;
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public void run() {
        try {
            run(httpClient, completionHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException;
}
