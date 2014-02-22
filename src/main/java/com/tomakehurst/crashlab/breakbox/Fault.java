package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.annotation.JsonGetter;

public abstract class Fault {

    public enum Direction { IN, OUT }
    public enum Type { NETWORK_FAILURE, SERVICE_FAILURE, FIREWALL_TIMEOUT, DELAY, PACKET_LOSS;}
    public enum Protocol { TCP, UDP }

    private final BreakBoxAdminClient adminClient;
    protected final String name;
    protected final Direction direction;
    protected final Protocol protocol;
    protected final int toPort;

    protected Fault(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        this.adminClient = adminClient;
        this.name = name;
        this.direction = direction;
        this.protocol = protocol;
        this.toPort = toPort;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    @JsonGetter("to_port")
    public int getToPort() {
        return toPort;
    }

    public abstract Type getType();

    public void enable() {
        adminClient.addFault(this);
    }
}
