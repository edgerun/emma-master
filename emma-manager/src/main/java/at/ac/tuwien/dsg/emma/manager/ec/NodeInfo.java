package at.ac.tuwien.dsg.emma.manager.ec;

import java.util.Objects;

/**
 * NodeInfo.
 */
public class NodeInfo {

    private String host;
    private int port;

    public NodeInfo() {
    }

    public NodeInfo(String host, int port) {
        this.host = host;
        this.port = port;
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
        NodeInfo nodeInfo = (NodeInfo) o;
        return port == nodeInfo.port &&
                Objects.equals(host, nodeInfo.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
