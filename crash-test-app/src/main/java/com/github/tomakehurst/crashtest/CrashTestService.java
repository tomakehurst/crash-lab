package com.github.tomakehurst.crashtest;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CrashTestService extends Application<CrashTestConfig> {

    @Override
    public void initialize(Bootstrap<CrashTestConfig> bootstrap) {
    }

    @Override
    public void run(CrashTestConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(new CrashTestResource(
                configuration.createHttpClient(environment, "wiremock-client"),
                configuration.getWireMockHost(),
                configuration.createWireMockClient()));
    }

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            new CrashTestService().run(new String[] { "server", "src/main/resources/crash-test.yaml" });
        } else {
            new CrashTestService().run(args);
        }

    }
}
