package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.IOException;

public abstract class HttpSteps implements Runnable {

    private final AsyncCompletionHandler<Response> completionHandler;
    private final AsyncHttpClient httpClient;
    private final String name;

    protected HttpSteps(String name) {
        this(name, new NoOpCompetionHandler(), new AsyncHttpClient());
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

    public abstract void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException;
}
