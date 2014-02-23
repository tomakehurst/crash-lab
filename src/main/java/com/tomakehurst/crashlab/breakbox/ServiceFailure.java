package com.tomakehurst.crashlab.breakbox;

public class ServiceFailure extends Fault {

    ServiceFailure(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        super(adminClient, name, direction, protocol, toPort);
    }

    @Override
    public Type getType() {
        return Type.SERVICE_FAILURE;
    }
}
