package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.nio.ByteBuffer;

import at.ac.tuwien.dsg.emma.io.Decode;
import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * MonitoringMessageReader.
 */
public class MonitoringMessageReader {
    public MonitoringMessage read(ByteBuffer buffer) {
        byte header = buffer.get();

        MonitoringPacketType type = MonitoringPacketType.fromHeader(header);
        switch (type) {
            case PING:
                return readPingMessage(buffer);
            case PONG:
                return readPongMessage(buffer);
            case PINGREQ:
                return readPingReqMessage(buffer);
            case PINGRESP:
                return readPingRespMessage(buffer);
            case RECONNECT:
                return readReconnectMessage(buffer);
            default:
                throw new UnsupportedOperationException("Unhandled message type: " + type);
        }
    }

    private MonitoringMessage readPingRespMessage(ByteBuffer buffer) {
        int id = buffer.getInt();
        int latency = buffer.getInt();

        return new PingRespMessage(id, latency);
    }

    private PingReqMessage readPingReqMessage(ByteBuffer buffer) {
        int id = buffer.getInt();
        String targetHost = Decode.readLengthEncodedString(buffer);
        int targetPort = buffer.getInt();

        PingReqMessage msg = new PingReqMessage(id);
        msg.setTargetHost(targetHost);
        msg.setTargetPort(targetPort);
        return msg;
    }

    private PongMessage readPongMessage(ByteBuffer buffer) {
        int pingId = buffer.getInt();
        long pingReceived = buffer.getLong();
        return new PongMessage(pingId, pingReceived);
    }

    private PingMessage readPingMessage(ByteBuffer buffer) {
        int id = buffer.getInt();
        return new PingMessage(id);
    }

    private MonitoringMessage readReconnectMessage(ByteBuffer buffer) {
        return new ReconnectMessage();
    }
}
