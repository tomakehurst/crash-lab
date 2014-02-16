package com.github.tomakehurst.crashtest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.dropwizard.Configuration;
import io.dropwizard.client.HttpClientBuilder;
import io.dropwizard.client.HttpClientConfiguration;
import io.dropwizard.setup.Environment;
import org.apache.http.client.HttpClient;

import javax.validation.constraints.NotNull;

public class CrashTestConfig extends Configuration {

    @JsonProperty
    @NotNull
    private String wireMockHost;

    @JsonProperty
    private HttpClientConfiguration clientConfig = new HttpClientConfiguration();

    public String getWireMockHost() {
        return wireMockHost;
    }

    public HttpClient createHttpClient(Environment environment, String name) {
        return new HttpClientBuilder(environment).using(clientConfig).build(name);
    }

    public WireMock createWireMockClient() {
        return new WireMock(wireMockHost, 8080);
    }

}
