package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.net.SocketAddress;

/**
 * AbstractMonitoringMessage.
 */
public abstract class AbstractMonitoringMessage implements MonitoringMessage {

    private SocketAddress source;
    private SocketAddress destination;

    @Override
    public SocketAddress getSource() {
        return source;
    }

    @Override
    public void setSource(SocketAddress source) {
        this.source = source;
    }

    @Override
    public SocketAddress getDestination() {
        return destination;
    }

    @Override
    public void setDestination(SocketAddress address) {
        this.destination = address;
    }
}
