package at.ac.tuwien.dsg.emma.monitoring.msg;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * PingRespMessage.
 */
public class PingRespMessage extends AbstractMonitoringMessage {

    private int requestId;

    private int latency;

    public PingRespMessage() {
    }

    public PingRespMessage(int requestId) {
        this.requestId = requestId;
    }

    public PingRespMessage(int requestId, int latency) {
        this.requestId = requestId;
        this.latency = latency;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.PINGRESP;
    }
}
