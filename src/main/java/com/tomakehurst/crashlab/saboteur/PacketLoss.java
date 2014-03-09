package com.tomakehurst.crashlab.saboteur;

public class PacketLoss extends Fault {

    private double probability;
    private int correlation;

    public static PacketLoss packetLoss(String name) {
        PacketLoss packetLoss = new PacketLoss();
        packetLoss.setName(name);
        return packetLoss;
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
