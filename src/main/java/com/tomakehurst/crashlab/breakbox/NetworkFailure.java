package com.tomakehurst.crashlab.breakbox;

public class NetworkFailure extends Fault {

    public static NetworkFailure networkFailure(String name) {
        NetworkFailure networkFailure = new NetworkFailure();
        networkFailure.setName(name);
        return networkFailure;
    }

    @Override
    public Type getType() {
        return Type.NETWORK_FAILURE;
    }
}
