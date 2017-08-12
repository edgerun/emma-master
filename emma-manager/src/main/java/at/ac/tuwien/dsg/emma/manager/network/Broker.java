package at.ac.tuwien.dsg.emma.manager.network;

/**
 * Broker.
 */
public class Broker extends Host {

    // TODO: keepalive
    private long lastSeen;

    private boolean isAlive;

    public Broker(String host, int port) {
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
        return "Broker{" +
                "id='" + getId() + '\'' +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
