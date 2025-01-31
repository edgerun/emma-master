package io.edgerun.emma.monitoring.msg;

import io.edgerun.emma.monitoring.MonitoringPacketType;

/**
 * UsageRequest.
 */
public class UsageRequest extends AbstractMonitoringMessage {

    private String hostId;

    public UsageRequest() {
    }

    public UsageRequest(String hostId) {
        this.hostId = hostId;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.USAGEREQ;
    }
}
