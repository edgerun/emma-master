package io.edgerun.emma.monitoring.msg;

import io.edgerun.emma.monitoring.MonitoringPacketType;

/**
 * PingMessage.
 */
public class PingMessage extends AbstractMonitoringMessage {

    private int id;

    public PingMessage() {
    }

    public PingMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.PING;
    }

    @Override
    public String toString() {
        return "PingMessage{" +
                "id=" + id +
                ", destination=" + getDestination() +
                '}';
    }
}
