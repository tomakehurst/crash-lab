package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static com.tomakehurst.crashlab.breakbox.PacketLoss.packetLoss;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

public class FaultTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void packet_loss_generates_correct_json() throws Exception {
        PacketLoss packetLoss = packetLoss("my-packet-loss");
        packetLoss.setDirection(Fault.Direction.IN);
        packetLoss.setProtocol(Fault.Protocol.TCP);
        packetLoss.setToPort(9191);
        packetLoss.correlation(56)
            .probability(0.22);

        String json = objectMapper.writeValueAsString(packetLoss);

        assertEquals(
                "{\n" +
                "   \"name\": \"my-packet-loss\",  \n" +
                "   \"type\": \"PACKET_LOSS\",     \n" +
                "   \"direction\": \"IN\",         \n" +
                "   \"protocol\": \"TCP\",         \n" +
                "   \"to_port\": 9191,             \n" +
                "   \"probability\": 0.22,         \n" +
                "   \"correlation\": 56            \n" +
                "}",
            json, NON_EXTENSIBLE);
    }

    @Test
    public void delay_with_all_params_generates_correct_json() throws Exception {
        Delay delay = Delay.delay("my-delay");
        delay.setDirection(Fault.Direction.IN);
        delay.setProtocol(Fault.Protocol.TCP);
        delay.setToPort(9191);
        delay.delay(150, TimeUnit.MILLISECONDS)
                .variance(10, TimeUnit.MILLISECONDS)
                .distribution(Delay.Distribution.PARETO)
                .correlation(15);

        String json = objectMapper.writeValueAsString(delay);

        assertEquals(
                "{\n" +
                "   \"name\": \"my-delay\",        \n" +
                "   \"type\": \"DELAY\",           \n" +
                "   \"direction\": \"IN\",         \n" +
                "   \"protocol\": \"TCP\",         \n" +
                "   \"to_port\": 9191,             \n" +
                "   \"delay\": 150,                \n" +
                "   \"variance\": 10,              \n" +
                "   \"distribution\": pareto,      \n" +
                "   \"correlation\": 15            \n" +
                "}",
            json, NON_EXTENSIBLE);
    }

    @Test
    public void delay_with_minimal_params_generates_correct_json() throws Exception {
        Delay delay = Delay.delay("my-delay");
        delay.setDirection(Fault.Direction.IN);
        delay.setProtocol(Fault.Protocol.TCP);
        delay.setToPort(8123);
        delay.delay(150, TimeUnit.MILLISECONDS);

        String json = objectMapper.writeValueAsString(delay);

        assertEquals(
                "{\n" +
                "   \"name\": \"my-delay\",        \n" +
                "   \"type\": \"DELAY\",           \n" +
                "   \"direction\": \"IN\",         \n" +
                "   \"protocol\": \"TCP\",         \n" +
                "   \"to_port\": 8123,             \n" +
                "   \"delay\": 150                 \n" +
                "}",
            json, NON_EXTENSIBLE);
    }

    @Test
    public void network_failure_generates_correct_json() throws Exception {
        NetworkFailure networkFailure = NetworkFailure.networkFailure("a-network-fail");
        networkFailure.setDirection(Fault.Direction.OUT);
        networkFailure.setProtocol(Fault.Protocol.UDP);
        networkFailure.setToPort(1111);

        String json = objectMapper.writeValueAsString(networkFailure);

        assertEquals(
                "{\n" +
                "   \"name\": \"a-network-fail\", \n" +
                "   \"type\": \"NETWORK_FAILURE\", \n" +
                "   \"direction\": \"OUT\",        \n" +
                "   \"protocol\": \"UDP\",         \n" +
                "   \"to_port\": 1111              \n" +
                "}",
            json, NON_EXTENSIBLE);

    }

    @Test
    public void service_failure_generates_correct_json() throws Exception {
        ServiceFailure serviceFailure = ServiceFailure.serviceFailure("a-service-fail");
        serviceFailure.setDirection(Fault.Direction.OUT);
        serviceFailure.setProtocol(Fault.Protocol.TCP);
        serviceFailure.setToPort(2222);

        String json = objectMapper.writeValueAsString(serviceFailure);

        assertEquals(
                "{\n" +
                "   \"name\": \"a-service-fail\", \n" +
                "   \"type\": \"SERVICE_FAILURE\", \n" +
                "   \"direction\": \"OUT\",        \n" +
                "   \"protocol\": \"TCP\",         \n" +
                "   \"to_port\": 2222              \n" +
                "}",
            json, NON_EXTENSIBLE);
    }

    @Test
    public void tcp_timeout_generates_correct_json() throws Exception {
        FirewallTimeout firewallTimeout = FirewallTimeout.firewallTimeout("tcp-timing-out");
        firewallTimeout.setDirection(Fault.Direction.OUT);
        firewallTimeout.setProtocol(Fault.Protocol.TCP);
        firewallTimeout.setToPort(3131);
        firewallTimeout.timeout(7, TimeUnit.MILLISECONDS);

        String json = objectMapper.writeValueAsString(firewallTimeout);

        assertEquals(
                "{\n" +
                "   \"name\": \"tcp-timing-out\",  \n" +
                "   \"type\": \"FIREWALL_TIMEOUT\",\n" +
                "   \"direction\": \"OUT\",        \n" +
                "   \"protocol\": \"TCP\",         \n" +
                "   \"to_port\": 3131,             \n" +
                "   \"timeout\": 7                 \n" +
                "}",
        json, NON_EXTENSIBLE);
    }
}
