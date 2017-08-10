package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.net.InetSocketAddress;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * MonitoringMessage.
 */
public interface MonitoringMessage {
    MonitoringPacketType getMonitoringPacketType();

    InetSocketAddress getSource();

    void setSource(InetSocketAddress address);

    InetSocketAddress getDestination();

    void setDestination(InetSocketAddress address);
}
