package io.edgerun.emma.monitoring.msg;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import io.edgerun.emma.io.Decode;
import io.edgerun.emma.monitoring.MonitoringPacketType;

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
            case USAGEREQ:
                return readUsageRequest(buffer);
            case USAGERESP:
                return readUsageResponse(buffer);
            case RECONNREQ:
                return readReconnectRequest(buffer);
            case RECONNACK:
                return readReconnectAck(buffer);
            default:
                throw new UnsupportedOperationException("Unhandled message type: " + type);
        }
    }

    private MonitoringMessage readUsageResponse(ByteBuffer buffer) {
        UsageResponse message = new UsageResponse();

        message.setHostId(Decode.readLengthEncodedString(buffer));
        message.setProcessors(buffer.getShort());
        message.setLoad(buffer.getFloat());
        message.setThroughputIn(buffer.getInt());
        message.setThroughputOut(buffer.getInt());

        return message;
    }

    private MonitoringMessage readUsageRequest(ByteBuffer buffer) {
        String hostId = Decode.readLengthEncodedString(buffer);
        return new UsageRequest(hostId);
    }

    private MonitoringMessage readPingRespMessage(ByteBuffer buffer) {
        int id = buffer.getInt();
        int latency = buffer.getInt();

        return new PingRespMessage(id, latency);
    }

    private PingReqMessage readPingReqMessage(ByteBuffer buffer) {
        int id = buffer.getInt();

        InetAddress targetHost = Decode.readInetAddress(buffer);
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

    private MonitoringMessage readReconnectRequest(ByteBuffer buffer) {
        String clientId = Decode.readLengthEncodedString(buffer);
        String brokerHost = Decode.readLengthEncodedString(buffer);
        return new ReconnectRequest(clientId, brokerHost);
    }

    private MonitoringMessage readReconnectAck(ByteBuffer buffer) {
        String clientId = Decode.readLengthEncodedString(buffer);
        String brokerHost = Decode.readLengthEncodedString(buffer);
        return new ReconnectAck(clientId, brokerHost);
    }
}
