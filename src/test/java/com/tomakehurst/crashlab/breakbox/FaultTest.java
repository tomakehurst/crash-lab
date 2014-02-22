package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

public class FaultTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void packet_loss_generates_correct_json() throws Exception {
        PacketLoss packetLoss = new PacketLoss(null, "my-packet-loss", Fault.Direction.IN, Fault.Protocol.TCP, 9191)
                .correlation(56)
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
        Delay delay = new Delay(null, "my-delay", Fault.Direction.IN, Fault.Protocol.TCP, 9191)
                .delay(150, TimeUnit.MILLISECONDS)
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

}
