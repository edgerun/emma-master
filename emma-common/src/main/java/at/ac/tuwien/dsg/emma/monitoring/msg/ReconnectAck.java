package at.ac.tuwien.dsg.emma.monitoring.msg;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * ReconnectAck.
 */
public class ReconnectAck extends AbstractMonitoringMessage {

    private String clientId;
    private String brokerHost;

    public ReconnectAck(ReconnectRequest request) {
        this(request.getClientId(), request.getBrokerHost());
        setDestination(request.getSource());
    }

    public ReconnectAck(String clientId, String brokerHost) {
        this.clientId = clientId;
        this.brokerHost = brokerHost;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBrokerHost() {
        return brokerHost;
    }

    public void setBrokerHost(String brokerHost) {
        this.brokerHost = brokerHost;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.RECONNACK;
    }

    @Override
    public String toString() {
        return "ReconnectAck{" +
                "clientId='" + clientId + '\'' +
                ", brokerHost='" + brokerHost + '\'' +
                '}';
    }
}
