package com.tomakehurst.crashlab;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RateTest {

    @Test
    public void should_calculate_period_correctly() {
        assertThat(Rate.rate(5).per(SECONDS).periodIn(MILLISECONDS), is(200L));
        assertThat(Rate.rate(120).per(MINUTES).periodIn(MILLISECONDS), is(500L));
    }
}
