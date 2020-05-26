package io.edgerun.emma.monitoring.msg;

import java.net.InetSocketAddress;

import io.edgerun.emma.monitoring.MonitoringPacketType;

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
