package at.ac.tuwien.dsg.emma.manager.model;

import java.util.Objects;

import at.ac.tuwien.dsg.emma.manager.network.Metrics;

/**
 * Value object to identify and store store information about a host.
 */
public class Host {

    private final String host;
    private final int port;
    private Metrics metrics;

    public Host(String host, int port) {
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

    public Metrics getMetrics() {
        if (metrics == null) {
            metrics = new Metrics();
        }

        return metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Host host = (Host) o;
        return port == host.port &&
                Objects.equals(this.host, host.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + getId() + '\'' +
                ", metrics=" + metrics +
                '}';
    }
}
