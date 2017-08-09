package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.net.SocketAddress;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * MonitoringMessage.
 */
public interface MonitoringMessage {
    MonitoringPacketType getMonitoringPacketType();

    SocketAddress getSource();

    void setSource(SocketAddress address);

    SocketAddress getDestination();

    void setDestination(SocketAddress address);
}
