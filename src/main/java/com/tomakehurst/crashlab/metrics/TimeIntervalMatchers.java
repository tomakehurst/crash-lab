package com.tomakehurst.crashlab.metrics;

import com.tomakehurst.crashlab.TimeInterval;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.concurrent.TimeUnit;

import static com.tomakehurst.crashlab.TimeInterval.interval;

public class TimeIntervalMatchers {

    public static Matcher<TimeInterval> lessThan(final long time, final TimeUnit timeUnit) {
        return new TypeSafeDiagnosingMatcher<TimeInterval>() {
            @Override
            protected boolean matchesSafely(TimeInterval item, Description mismatchDescription) {
                boolean match = item.lessThan(interval(time, timeUnit));
                if (!match) {
                    mismatchDescription.appendText("interval was " + item.timeIn(timeUnit) + " " + timeUnit.toString().toLowerCase());
                }

                return match;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a time interval less than " + time + " " + timeUnit.toString().toLowerCase());
            }
        };
    }
}
