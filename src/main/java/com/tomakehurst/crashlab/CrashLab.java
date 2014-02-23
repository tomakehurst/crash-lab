package com.tomakehurst.crashlab;

import com.google.common.base.Stopwatch;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import java.io.IOException;
import java.util.concurrent.*;

import static com.tomakehurst.crashlab.utils.Exceptions.throwUnchecked;
import static java.util.concurrent.TimeUnit.*;

public class CrashLab {

    public void run(TimeInterval interval, Rate rate, final HttpSteps httpSteps) {
        final ConcurrentLinkedQueue<ListenableFuture<Response>> responseFutures = new ConcurrentLinkedQueue<ListenableFuture<Response>>();
        HttpSteps trackingSteps = new HttpSteps(httpSteps.name()) {
            public ListenableFuture<Response> run(AsyncHttpClient http, AsyncCompletionHandler<Response> completionHandler) throws IOException {
                ListenableFuture<Response> responseListenableFuture = httpSteps.run(http, completionHandler);
                responseFutures.offer(responseListenableFuture);
                return responseListenableFuture;
            }
        };


        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Stopwatch stopwatch = Stopwatch.createStarted();
        executor.scheduleAtFixedRate(trackingSteps, 0, rate.periodIn(NANOSECONDS), NANOSECONDS);
        int stepCount = 0;
        try {
            Thread.sleep(interval.timeIn(MILLISECONDS));
            executor.shutdown();
            executor.awaitTermination(interval.nanos(), NANOSECONDS);
            while (!responseFutures.isEmpty()) {
                ListenableFuture<Response> responseFuture = responseFutures.poll();
                stepCount++;
                responseFuture.get();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted while running " + httpSteps.name());
        } catch (ExecutionException e) {
            throwUnchecked(e);
        }

        System.out.printf("Actual run time for '%s': %d seconds\n", httpSteps.name(), stopwatch.elapsed(SECONDS));
        long actualRate = stepCount / stopwatch.elapsed(rate.unit());
        System.out.printf("Actual rate: %d/%s\n", actualRate, singularUnitName(rate.unit()));
    }

    private String singularUnitName(TimeUnit unit) {
        return unit.toString().toLowerCase().substring(0, unit.toString().length() - 1);
    }
}
