package at.ac.tuwien.dsg.emma.monitoring.msg;

import java.nio.ByteBuffer;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * MonitoringMessageReader.
 */
public class MonitoringMessageReader {
    public MonitoringMessage read(ByteBuffer buffer) {
        byte header = buffer.get();

        switch (MonitoringPacketType.fromHeader(header)) {
            case PING:
                return readPingMessage(buffer);
            case PONG:
                return readPongMessage(buffer);
            default:
                throw new UnsupportedOperationException();
        }
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
}
