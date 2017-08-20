package at.ac.tuwien.dsg.emma;

import java.io.Serializable;
import java.util.Objects;

/**
 * NodeInfo.
 */
public class NodeInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String host;
    private int port;
    private int monitoringPort;

    public NodeInfo() {
    }

    public NodeInfo(String host, int port, int monitoringPort) {
        this.host = host;
        this.port = port;
        this.monitoringPort = monitoringPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public void setMonitoringPort(int monitoringPort) {
        this.monitoringPort = monitoringPort;
    }

    public boolean isHostWildcard() {
        return host == null || host.isEmpty() || "0".equals(host) || "0.0.0.0".equals(host);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port &&
                monitoringPort == nodeInfo.monitoringPort &&
                Objects.equals(host, nodeInfo.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, monitoringPort);
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", monitoringPort=" + monitoringPort +
                '}';
    }
}
