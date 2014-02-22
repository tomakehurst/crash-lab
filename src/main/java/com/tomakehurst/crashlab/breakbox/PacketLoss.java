package com.tomakehurst.crashlab.breakbox;

public class PacketLoss extends Fault {

    private double probability;
    private int correlation;

    PacketLoss(BreakBoxAdminClient adminClient, String name, Direction direction, Protocol protocol, int toPort) {
        super(adminClient, name, direction, protocol, toPort);
    }

    public PacketLoss probability(double probability) {
        this.probability = probability;
        return this;
    }

    public PacketLoss correlation(int correlation) {
        this.correlation = correlation;
        return this;
    }

    public double getProbability() {
        return probability;
    }

    public int getCorrelation() {
        return correlation;
    }

    @Override
    public Type getType() {
        return Type.PACKET_LOSS;
    }
}
