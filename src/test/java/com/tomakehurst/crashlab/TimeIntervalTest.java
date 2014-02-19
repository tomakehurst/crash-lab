package com.tomakehurst.crashlab;

import org.junit.Test;

import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimeIntervalTest {

    @Test
    public void supports_less_than_test() {
        assertTrue(new TimeInterval(5, SECONDS).lessThan(new TimeInterval(6500, MILLISECONDS)));
        assertFalse(new TimeInterval(300, MICROSECONDS).lessThan(new TimeInterval(450, NANOSECONDS)));
    }
}
