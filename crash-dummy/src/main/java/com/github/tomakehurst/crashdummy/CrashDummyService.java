package com.github.tomakehurst.crashdummy;


import com.codahale.metrics.Histogram;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CrashDummyService extends Application<CrashDummyConfig> {

    @Override
    public void initialize(Bootstrap<CrashDummyConfig> bootstrap) {
    }

    @Override
    public void run(CrashDummyConfig configuration, Environment environment) throws Exception {
        environment.jersey().register(new CrashDummyResource(
                configuration.createHttpClient(environment, "wiremock-client"),
                configuration.createShortTimeoutHttpClient(environment, "wiremock-short-timeout-client"),
                configuration.createWireMockClient(), configuration.getWireMockHost()
        ));

        Histogram histogram = environment.metrics().histogram("example-histogram");
        histogram.update(1);
        histogram.update(2);
        histogram.update(3);
        histogram.update(4);
        histogram.update(5);

    }

    public static void main(String... args) throws Exception {
        if (args.length == 0) {
            new CrashDummyService().run(new String[] { "server", "src/main/resources/crash-test.yaml" });
        } else {
            new CrashDummyService().run(args);
        }

    }
}
