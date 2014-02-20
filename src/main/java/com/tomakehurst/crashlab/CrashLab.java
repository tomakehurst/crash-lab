package com.tomakehurst.crashlab;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

public class CrashLab {

    public void run(TimeInterval interval, Rate rate, HttpSteps httpSteps) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(httpSteps, 0, rate.periodIn(NANOSECONDS), NANOSECONDS);
        try {
            Thread.sleep(interval.timeIn(MILLISECONDS));
        } catch (InterruptedException e) {
            System.out.println("Interrupted while running " + httpSteps.name());
        }

        executor.shutdown();
    }
}
