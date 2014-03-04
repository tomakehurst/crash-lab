package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.annotation.JsonGetter;

public abstract class Fault {

    public enum Direction { IN, OUT }
    public enum Type { NETWORK_FAILURE, SERVICE_FAILURE, FIREWALL_TIMEOUT, DELAY, PACKET_LOSS;}
    public enum Protocol { TCP, UDP }

    protected String name;
    protected Direction direction;
    protected Protocol protocol;
    protected int toPort;

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

    public <T extends Fault> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public <T extends Fault> T setDirection(Direction direction) {
        this.direction = direction;
        return (T) this;
    }

    public <T extends Fault> T setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return (T) this;
    }

    public <T extends Fault> T setToPort(int toPort) {
        this.toPort = toPort;
        return (T) this;
    }
}
