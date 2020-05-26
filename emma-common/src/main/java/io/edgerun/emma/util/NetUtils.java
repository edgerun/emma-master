package io.edgerun.emma.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;

/**
 * NetUtils.
 */
public final class NetUtils {

    /**
     * Finds a random open TCP port on any local interface.
     *
     * @return a port
     */
    public static int getRandomTcpPort() {
        return getRandomTcpPort((InetAddress) null);
    }

    /**
     * Finds a random open TCP port on the given address.
     *
     * @param address the address as a resolvable name
     * @return a port
     * @throws UnknownHostException if the host is not known
     */
    public static int getRandomTcpPort(String address) throws UnknownHostException {
        return getRandomTcpPort(InetAddress.getByName(address));
    }

    /**
     * Finds a random open tcp port on the given address.
     *
     * @param iface the address
     * @return a port
     */
    public static int getRandomTcpPort(InetAddress iface) {
        try (ServerSocket socket = new ServerSocket(0, 50, iface)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static int getRandomUdpPort() throws UnknownHostException {
        return getRandomUdpPort((InetAddress) null);
    }

    public static int getRandomUdpPort(String address) throws UnknownHostException {
        return getRandomUdpPort(InetAddress.getByName(address));
    }

    public static int getRandomUdpPort(InetAddress iface) {
        try (DatagramSocket socket = new DatagramSocket(0, iface)) {
            return socket.getLocalPort();
        } catch (SocketException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Calls {@link SocketChannel#getRemoteAddress()} and casts it to an {@link InetSocketAddress}.
     *
     * @param channel the channel
     * @return The remote address; null if the channel's socket is not connected
     */
    public static InetSocketAddress getRemoteAddress(SocketChannel channel) {
        try {
            return (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Calls {@link SocketChannel#getLocalAddress()} ()} and casts it to an {@link InetSocketAddress}.
     *
     * @param channel the channel
     * @return The remote address; null if the channel's socket is not connected
     */
    public static InetSocketAddress getLocalAddress(SocketChannel channel) {
        try {
            return (InetSocketAddress) channel.getLocalAddress();
        } catch (IOException e) {
            return null;
        }
    }

    public static InetSocketAddress parseSocketAddress(String socketAddress) {
        String[] parts = socketAddress.split(":");

        if (parts.length != 2) {
            throw new IllegalArgumentException(socketAddress + " is not a valid socket address");
        }

        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Port is valid", e);
        }

        return new InetSocketAddress(host, port);
    }

    private NetUtils() {
        // util class
    }

}
