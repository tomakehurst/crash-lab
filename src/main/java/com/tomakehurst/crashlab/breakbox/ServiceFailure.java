package com.tomakehurst.crashlab.breakbox;

public class ServiceFailure extends Fault {

    public static ServiceFailure serviceFailure(String name) {
        ServiceFailure serviceFailure = new ServiceFailure();
        serviceFailure.setName(name);
        return serviceFailure;
    }

    @Override
    public Type getType() {
        return Type.SERVICE_FAILURE;
    }
}
