package at.ac.tuwien.dsg.emma.manager.broker;

import at.ac.tuwien.dsg.emma.manager.ec.NodeInfo;

/**
 * BrokerInfo.
 */
public class BrokerInfo extends NodeInfo {

    // TODO: keepalive
    private long lastSeen;

    private boolean isAlive;

    public BrokerInfo() {
        super();
    }

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
