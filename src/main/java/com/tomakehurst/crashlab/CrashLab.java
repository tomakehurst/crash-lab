package com.tomakehurst.crashlab;

import com.google.common.base.Stopwatch;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.*;

public class CrashLab {

    public void run(TimeInterval interval, Rate rate, final HttpSteps httpSteps) {
        final AtomicLong count = new AtomicLong(0);
        HttpSteps countingSteps = new HttpSteps(httpSteps.name()) {
            public void run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                httpSteps.run(http, completionHandler);
                count.incrementAndGet();
            }
        };

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Stopwatch stopwatch = Stopwatch.createStarted();
        executor.scheduleAtFixedRate(countingSteps, 0, rate.periodIn(NANOSECONDS), NANOSECONDS);
        try {
            Thread.sleep(interval.timeIn(MILLISECONDS));
        } catch (InterruptedException e) {
            System.out.println("Interrupted while running " + httpSteps.name());
        }

        executor.shutdown();
        stopwatch.stop();

        System.out.printf("Actual run time for '%s': %d seconds\n", httpSteps.name(), stopwatch.elapsed(SECONDS));
        long actualRate = count.get() / stopwatch.elapsed(rate.unit());
        System.out.printf("Actual rate: %d/%s\n", actualRate, singularUnitName(rate.unit()));
    }

    private String singularUnitName(TimeUnit unit) {
        return unit.toString().toLowerCase().substring(0, unit.toString().length() - 1);
    }
}
