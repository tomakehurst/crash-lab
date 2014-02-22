package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Iterables.transform;
import static com.tomakehurst.crashlab.utils.Exceptions.throwUnchecked;
import static java.util.concurrent.TimeUnit.SECONDS;

public class BreakBoxAdminClient {

    private final AsyncHttpClient client;
    private final List<String> hosts;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BreakBoxAdminClient(AsyncHttpClient client, List<String> hosts) {
        this.client = client;
        this.hosts = hosts;
    }

    @SuppressWarnings("unchecked")
    public <T extends Fault> void addFault(final T fault) {
        executeRequestForAllHosts(new HttpClientAction("Add fault") {
            public ListenableFuture<Response> doWithClient(AsyncHttpClient client, String host) throws IOException {
                StringBuilder url = new StringBuilder().append("http://").append(host).append(":6660").append("/");
                return client.preparePost(url.toString())
                    .setBody(objectMapper.writeValueAsBytes(fault))
                    .execute();
            }
        });
    }

    public void reset() {
        executeRequestForAllHosts(new HttpClientAction("Reset") {
            public ListenableFuture<Response> doWithClient(AsyncHttpClient client, String host) throws IOException {
                StringBuilder url = new StringBuilder().append("http://").append(host).append(":6660").append("/");
                return client.prepareDelete(url.toString()).execute();
            }
        });
    }

    private void executeRequestForAllHosts(final HttpClientAction httpClientAction) {
        Iterable<ListenableFuture<Response>> responseFutures = transform(hosts, new Function<String, ListenableFuture<Response>>() {
            public ListenableFuture<Response> apply(String host) {
                try {
                    return httpClientAction.doWithClient(client, host);
                } catch (IOException e) {
                    return throwUnchecked(e, ListenableFuture.class);
                }
            }
        });

        for (ListenableFuture<Response> responseFuture: responseFutures) {
            try {
                Response response = responseFuture.get(10, SECONDS);
                System.out.println(httpClientAction.name + ": " + response.getStatusText());
            } catch (Exception e) {
                e.printStackTrace();
                throwUnchecked(e);
            }
        }
    }

    private static abstract class HttpClientAction {

        public final String name;

        protected HttpClientAction(String name) {
            this.name = name;
        }

        public abstract ListenableFuture<Response> doWithClient(AsyncHttpClient client, String host) throws IOException;
    }
}
