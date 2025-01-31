package io.edgerun.emma.controller.model;

/**
 * Broker.
 */
public class Broker extends Host {

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
                ", monitoringPort=" + getMonitoringPort() +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
