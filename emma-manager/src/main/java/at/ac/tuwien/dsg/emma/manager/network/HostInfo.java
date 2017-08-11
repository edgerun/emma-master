package at.ac.tuwien.dsg.emma.manager.network;

import java.util.Objects;

/**
 * Value object to identify and store store information about a host.
 */
public class HostInfo {

    private final String host;
    private final int port;

    public HostInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getId() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HostInfo hostInfo = (HostInfo) o;
        return port == hostInfo.port &&
                Objects.equals(host, hostInfo.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
