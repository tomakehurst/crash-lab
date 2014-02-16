package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public interface HttpSteps {
    void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws Exception;
}
