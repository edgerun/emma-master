package at.ac.tuwien.dsg.emma.manager.model;

/**
 * Broker.
 */
public class Broker extends Host {

    private int monitoringPort;

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

    public int getMonitoringPort() {
        return monitoringPort;
    }

    public void setMonitoringPort(int monitoringPort) {
        this.monitoringPort = monitoringPort;
    }

    @Override
    public String toString() {
        return "Broker{" +
                "id='" + getId() + '\'' +
                ", monitoringPort=" + monitoringPort +
                ", lastSeen=" + lastSeen +
                ", isAlive=" + isAlive +
                '}';
    }
}
