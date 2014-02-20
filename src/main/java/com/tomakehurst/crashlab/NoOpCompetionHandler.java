package com.tomakehurst.crashlab;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;

public class NoOpCompetionHandler extends AsyncCompletionHandler<Response> {
    @Override
    public Response onCompleted(Response response) throws Exception {
        return response;
    }
}
