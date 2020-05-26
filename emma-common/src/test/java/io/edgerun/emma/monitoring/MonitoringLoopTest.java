package io.edgerun.emma.monitoring;

import static org.junit.Assert.assertEquals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.edgerun.emma.monitoring.msg.PingMessage;
import io.edgerun.emma.monitoring.msg.PongMessage;
import io.edgerun.emma.util.Concurrent;
import org.junit.Test;

/**
 * MonitoringLoopTest.
 */
public class MonitoringLoopTest {
    @Test
    public void test() throws Exception {

        List<PongMessage> pongs = new ArrayList<>();
        List<PingMessage> pings = new ArrayList<>();

        MonitoringLoop l1 = new MonitoringLoop(new InetSocketAddress(43201)).setReadHandler(new MonitoringMessageHandlerAdapter() {
            @Override
            public void onMessage(MonitoringLoop loop, PingMessage message) {
                pings.add(message);
                PongMessage pong = new PongMessage(message.getId(), System.currentTimeMillis());
                pong.setDestination(message.getSource());
                loop.send(pong);
            }

            @Override
            public void onMessage(MonitoringLoop loop, PongMessage message) {
                pongs.add(message);
            }
        });
        MonitoringLoop l2 = new MonitoringLoop(new InetSocketAddress(43202)).setReadHandler(new MonitoringMessageHandlerAdapter() {
            @Override
            public void onMessage(MonitoringLoop loop, PingMessage message) {
                pings.add(message);
            }

            @Override
            public void onMessage(MonitoringLoop loop, PongMessage message) {
                pongs.add(message);
            }
        });

        Thread t1 = new Thread(l1);
        Thread t2 = new Thread(l2);

        t1.start();
        t2.start();

        Concurrent.sleep(1000);

        PingMessage msg;

        msg = new PingMessage(10);
        msg.setDestination(new InetSocketAddress(43201));
        l2.send(msg);

        msg = new PingMessage(11);
        msg.setDestination(new InetSocketAddress(43201));
        l2.send(msg);

        Thread.sleep(1000);

        l1.close();
        l2.close();

        t1.join();
        t2.join();


        assertEquals(2, pings.size());
        assertEquals(2, pongs.size());

        assertEquals(10, pongs.get(0).getPingId());
        assertEquals(11, pongs.get(1).getPingId());
    }
}