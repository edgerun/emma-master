package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.nio.ByteBuffer;

import at.ac.tuwien.dsg.emma.io.Encode;
import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * MonitoringMessageWriter.
 */
public class MonitoringMessageWriter {

    public void write(ByteBuffer buf, MonitoringMessage message) {
        MonitoringPacketType type = message.getMonitoringPacketType();

        switch (type) {
            case PING:
                write(buf, (PingMessage) message);
                return;
            case PONG:
                write(buf, (PongMessage) message);
                return;
            case PINGREQ:
                write(buf, (PingReqMessage) message);
                return;
            case PINGRESP:
                write(buf, (PingRespMessage) message);
                return;
            case RECONNECT:
                write(buf, (ReconnectMessage) message);
                return;
            default:
                throw new UnsupportedOperationException("Unhandled message type: " + type);
        }
    }

    public void write(ByteBuffer buf, PingMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getId());
    }

    public void write(ByteBuffer buf, PingReqMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getRequestId());
        Encode.writeLengthEncodedString(buf, message.getTargetHost());
        buf.putInt(message.getTargetPort());
    }

    public void write(ByteBuffer buf, PongMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getPingId());
        buf.putLong(message.getPingReceived());
    }

    public void write(ByteBuffer buf, PingRespMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        buf.putInt(message.getRequestId());
        buf.putInt(message.getLatency());
    }

    public void write(ByteBuffer buf, ReconnectMessage message) {
        buf.put(message.getMonitoringPacketType().toHeader());
    }
}
