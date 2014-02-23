package com.tomakehurst.crashlab.breakbox;

import com.tomakehurst.crashlab.TimeInterval;

import java.util.concurrent.TimeUnit;

import static com.tomakehurst.crashlab.TimeInterval.interval;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FirewallTimeout extends Fault {

    private TimeInterval tcpTimeout;

    FirewallTimeout(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        super(adminClient, name, direction, protocol, toPort);
    }

    @Override
    public Type getType() {
        return Type.FIREWALL_TIMEOUT;
    }

    public FirewallTimeout timeout(long time, TimeUnit timeUnit) {
        this.tcpTimeout = interval(time, timeUnit);
        return this;
    }

    public long getTimeout() {
        return tcpTimeout.timeIn(MILLISECONDS);
    }
}
