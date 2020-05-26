package io.edgerun.emma.monitoring.msg;

import java.net.InetSocketAddress;

/**
 * AbstractMonitoringMessage.
 */
public abstract class AbstractMonitoringMessage implements MonitoringMessage {

    private InetSocketAddress source;
    private InetSocketAddress destination;

    @Override
    public InetSocketAddress getSource() {
        return source;
    }

    @Override
    public void setSource(InetSocketAddress source) {
        this.source = source;
    }

    @Override
    public InetSocketAddress getDestination() {
        return destination;
    }

    @Override
    public void setDestination(InetSocketAddress address) {
        this.destination = address;
    }
}
