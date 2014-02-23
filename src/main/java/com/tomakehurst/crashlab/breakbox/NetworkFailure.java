package com.tomakehurst.crashlab.breakbox;

public class NetworkFailure extends Fault {

    NetworkFailure(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        super(adminClient, name, direction, protocol, toPort);
    }

    @Override
    public Type getType() {
        return Type.NETWORK_FAILURE;
    }
}
