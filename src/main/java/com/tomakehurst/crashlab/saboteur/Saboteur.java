package com.tomakehurst.crashlab.saboteur;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.net.HostAndPort;
import com.ning.http.client.AsyncHttpClient;

import java.util.List;

import static com.google.common.collect.FluentIterable.of;
import static java.util.Arrays.asList;

public class Saboteur {

    public static final int DEFAULT_PORT = 6660;

    private final String name;

    private final Fault.Direction direction;
    private final Fault.Protocol protocol;
    private final int destPort;
    private final SaboteurAdminClient adminClient;

    public Saboteur(String name, Fault.Direction direction, Fault.Protocol protocol, int destPort, List<HostAndPort> hosts) {
        this.name = name;
        this.direction = direction;
        this.protocol = protocol;
        this.destPort = destPort;
        adminClient = new SaboteurAdminClient(new AsyncHttpClient(), hosts);
    }

    public static Saboteur defineClient(String name, int destPort, HostAndPort... hostsAndPorts) {
        return new Saboteur(name, Fault.Direction.OUT, Fault.Protocol.TCP, destPort, asList(hostsAndPorts));
    }

    public static Saboteur defineClient(String name, int destPort, String... hosts) {
        return new Saboteur(name, Fault.Direction.OUT, Fault.Protocol.TCP, destPort, toHostAndPortList(hosts));
    }

    public static Saboteur defineClient(String name, Fault.Protocol protocol, int destPort, String... hosts) {
        return new Saboteur(name, Fault.Direction.OUT, protocol, destPort, toHostAndPortList(hosts));
    }

    public static Saboteur defineService(String name, int destPort, String... hosts) {
        return new Saboteur(name, Fault.Direction.IN, Fault.Protocol.TCP, destPort, toHostAndPortList(hosts));
    }

    public static Saboteur defineService(String name, Fault.Protocol protocol, int destPort, String... hosts) {
        return new Saboteur(name, Fault.Direction.IN, protocol, destPort, toHostAndPortList(hosts));
    }

    private static List<HostAndPort> toHostAndPortList(String... hosts) {
        return of(hosts).transform(new Function<String, HostAndPort>() {
            @Override
            public HostAndPort apply(String input) {
                return HostAndPort.fromParts(input, DEFAULT_PORT);
            }
        }).toList();
    }

    public void reset() {
        adminClient.reset();
    }

    public <T extends Fault> void addFault(T fault) {
        fault.setDirection(direction);
        fault.setProtocol(protocol);
        fault.setToPort(destPort);
        adminClient.addFault(fault);
    }


}
