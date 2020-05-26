package io.edgerun.emma.monitoring.msg;

import io.edgerun.emma.monitoring.MonitoringPacketType;

/**
 * ReconnectRequest.
 */
public class ReconnectRequest extends AbstractMonitoringMessage {

    private String clientId;
    private String brokerHost;

    public ReconnectRequest(String clientId, String brokerHost) {
        this.clientId = clientId;
        this.brokerHost = brokerHost;
    }

    public String getClientId() {
        return clientId;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.RECONNREQ;
    }

    @Override
    public String toString() {
        return "ReconnectRequest{" +
                "clientId='" + clientId + '\'' +
                ", brokerHost='" + brokerHost + '\'' +
                ", destination='" + getDestination() + '\'' +
                '}';
    }
}
