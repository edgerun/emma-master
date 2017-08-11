package at.ac.tuwien.dsg.emma.manager.broker;

/**
 * BrokerInfo.
 */
public class BrokerInfo {

    private String host;
    private int port;

    // TODO: keepalive
    private long lastSeen;

    private boolean isAlive;

    public BrokerInfo() {
    }

    public BrokerInfo(String host, int port) {
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

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getId() {
        return host + ":" + port;
    }

    @Override
    public String toString() {
        return "BrokerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
