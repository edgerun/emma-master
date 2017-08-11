package at.ac.tuwien.dsg.emma.manager.network;

/**
 * BrokerInfo.
 */
public class BrokerInfo extends HostInfo {

    // TODO: keepalive
    private long lastSeen;

    private boolean isAlive;

    public BrokerInfo(String host, int port) {
        super(host, port);
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


    @Override
    public String toString() {
        return "BrokerInfo{" +
                "id='" + getId() + '\'' +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
