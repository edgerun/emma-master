package io.edgerun.emma.monitoring.msg;

import io.edgerun.emma.monitoring.MonitoringPacketType;

/**
 * PongMessage.
 */
public class PongMessage extends AbstractMonitoringMessage {

    private int pingId;
    private long pingReceived;

    public PongMessage() {
    }

    public PongMessage(int pingId, long pingReceived) {
        this.pingId = pingId;
        this.pingReceived = pingReceived;
    }

    public int getPingId() {
        return pingId;
    }

    public void setPingId(int pingId) {
        this.pingId = pingId;
    }

    public long getPingReceived() {
        return pingReceived;
    }

    public void setPingReceived(long pingReceived) {
        this.pingReceived = pingReceived;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.PONG;
    }
}
