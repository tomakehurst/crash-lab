package com.tomakehurst.crashlab.breakbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

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

}
