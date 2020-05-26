package io.edgerun.emma.monitoring.msg;

import java.nio.ByteBuffer;

import io.edgerun.emma.io.Encode;
import io.edgerun.emma.monitoring.MonitoringPacketType;

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
            case RECONNREQ:
                write(buf, (ReconnectRequest) message);
                return;
            case RECONNACK:
                write(buf, (ReconnectAck) message);
                return;
            case USAGEREQ:
                write(buf, (UsageRequest) message);
                return;
            case USAGERESP:
                write(buf, (UsageResponse) message);
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
        Encode.writeInetAddress(buf, message.getTargetHost());
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

    public void write(ByteBuffer buf, ReconnectRequest message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        Encode.writeLengthEncodedString(buf, message.getClientId());
        Encode.writeLengthEncodedString(buf, message.getBrokerHost());
    }

    public void write(ByteBuffer buf, ReconnectAck message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        Encode.writeLengthEncodedString(buf, message.getClientId());
        Encode.writeLengthEncodedString(buf, message.getBrokerHost());
    }

    public void write(ByteBuffer buf, UsageRequest message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        Encode.writeLengthEncodedString(buf, message.getHostId());
    }

    public void write(ByteBuffer buf, UsageResponse message) {
        buf.put(message.getMonitoringPacketType().toHeader());
        Encode.writeLengthEncodedString(buf, message.getHostId());

        buf.putShort((short) message.getProcessors());
        buf.putFloat(message.getLoad());
        buf.putInt(message.getThroughputIn());
        buf.putInt(message.getThroughputOut());
    }
}
