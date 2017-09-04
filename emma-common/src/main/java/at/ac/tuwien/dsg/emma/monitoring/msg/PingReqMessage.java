package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.net.InetAddress;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * PingReqMessage.
 */
public class PingReqMessage extends AbstractMonitoringMessage {

    private int requestId;

    private InetAddress targetHost;
    private int targetPort;

    public PingReqMessage() {

    }

    public PingReqMessage(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public InetAddress getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(InetAddress targetHost) {
        this.targetHost = targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.PINGREQ;
    }

    @Override
    public String toString() {
        return "PingReqMessage{" +
                "requestId=" + requestId +
                ", target='" + targetHost + ":" + targetPort + '\'' +
                ", source='" + getDestination() + '\'' +
                '}';
    }
}
