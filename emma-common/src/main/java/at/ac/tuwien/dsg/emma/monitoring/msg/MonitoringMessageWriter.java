package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.nio.ByteBuffer;

/**
 * MonitoringMessageWriter.
 */
public class MonitoringMessageWriter {

    public void write(ByteBuffer buf, MonitoringMessage message) {
        switch (message.getMonitoringPacketType()) {
            case PING:
                write(buf, (PingMessage) message);
                return;
            case PONG:
                write(buf, (PongMessage) message);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void write(ByteBuffer buf, PingMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getId());
    }

    public void write(ByteBuffer buf, PongMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getPingId());
        buf.putLong(message.getPingReceived());
    }
}
