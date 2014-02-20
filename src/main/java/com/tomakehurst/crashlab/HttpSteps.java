package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.IOException;

public abstract class HttpSteps implements Runnable {

    private final AsyncCompletionHandler<Response> completionHandler;
    private final AsyncHttpClient httpClient;

    protected HttpSteps() {
        this(new NoOpCompetionHandler(), new AsyncHttpClient());
    }

    protected HttpSteps(AsyncCompletionHandler<Response> completionHandler, AsyncHttpClient httpClient) {
        this.completionHandler = completionHandler;
        this.httpClient = httpClient;
    }

    @Override
    public void run() {
        try {
            run(httpClient, completionHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String name();

    protected abstract void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException;
}
