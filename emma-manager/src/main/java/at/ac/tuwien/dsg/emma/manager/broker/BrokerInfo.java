package at.ac.tuwien.dsg.emma.manager.broker;

/**
 * BrokerInfo.
 */
public class BrokerInfo {

    private String address;
    private int port;

    // TODO: keepalive
    private long lastSeen;

    private boolean isAlive;

    public BrokerInfo() {
    }

    public BrokerInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        return address + ":" + port;
    }

    @Override
    public String toString() {
        return "BrokerInfo{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
