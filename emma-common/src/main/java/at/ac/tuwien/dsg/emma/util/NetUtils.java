package at.ac.tuwien.dsg.emma.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

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

    private NetUtils() {
        // util class
    }

}
