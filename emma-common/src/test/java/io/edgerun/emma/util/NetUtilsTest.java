package io.edgerun.emma.util;

import static org.junit.Assert.assertNotEquals;

import java.net.InetAddress;

import org.junit.Test;

/**
 * NetUtilsTest.
 */
public class NetUtilsTest {
    @Test
    public void randomTcpPort() throws Exception {
        int port1 = NetUtils.getRandomTcpPort();
        int port2 = NetUtils.getRandomTcpPort("127.0.0.1");
        int port3 = NetUtils.getRandomTcpPort(InetAddress.getLocalHost());

        assertNotEquals(0, port1);
        assertNotEquals(0, port2);
        assertNotEquals(0, port3);

        System.out.println("Random tcp ports:");
        System.out.println("*: " + port1);
        System.out.println("127.0.0.1: " + port2);
        System.out.println("localhost: " + port3);
    }

    @Test
    public void randomUdpPort() throws Exception {
        int port1 = NetUtils.getRandomUdpPort();
        int port2 = NetUtils.getRandomUdpPort("127.0.0.1");
        int port3 = NetUtils.getRandomUdpPort(InetAddress.getLocalHost());

        assertNotEquals(0, port1);
        assertNotEquals(0, port2);
        assertNotEquals(0, port3);

        System.out.println("Random udp ports:");
        System.out.println("*: " + port1);
        System.out.println("127.0.0.1: " + port2);
        System.out.println("localhost: " + port3);
    }
}