package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * MonitoringMessageWriterTest.
 */
public class MonitoringMessageWriterTest {

    @Test
    public void test() throws Exception {
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 1337);

        System.out.println(Arrays.toString(addr.getAddress().getAddress()));
    }

    @Test
    public void size() throws Exception {
        PingReqMessage pingreq = new PingReqMessage();
        pingreq.setRequestId(42);
        pingreq.setTargetHost(InetAddress.getByName("255.255.255.255"));
        pingreq.setTargetPort(65535);

        PingRespMessage pingresp = new PingRespMessage();
        pingresp.setRequestId(42);
        pingresp.setLatency(120);

        PingMessage ping = new PingMessage();
        ping.setId(42);

        PongMessage pong = new PongMessage();
        ping.setId(42);

        UsageRequest usagereq = new UsageRequest();
        usagereq.setHostId("255.255.255.255:65535");

        UsageResponse usageresp = new UsageResponse();
        usageresp.setLoad(0.01f);
        usageresp.setProcessors(2);
        usageresp.setThroughputIn(1);
        usageresp.setThroughputOut(1);
        usageresp.setHostId("255.255.255.255:65535");

        ReconnectRequest reconnreq = new ReconnectRequest("255.255.255.255:65535", "255.255.255.255:65535");
        ReconnectAck reconnack = new ReconnectAck(reconnreq);


        Collection<MonitoringMessage> messages = new ArrayList<>();

        messages.add(pingreq);
        messages.add(pingresp);
        messages.add(ping);
        messages.add(pong);
        messages.add(usagereq);
        messages.add(usageresp);
        messages.add(reconnreq);
        messages.add(reconnack);

        MonitoringMessageWriter writer = new MonitoringMessageWriter();

        for (MonitoringMessage message : messages) {
            ByteBuffer buf = ByteBuffer.allocate(128);

            writer.write(buf, message);
            buf.flip();

            System.out.printf("%-20s: %d%n", message.getClass().getSimpleName(), buf.remaining());
        }

    }
}