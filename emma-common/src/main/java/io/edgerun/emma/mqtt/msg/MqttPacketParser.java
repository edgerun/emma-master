package io.edgerun.emma.mqtt.msg;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import io.edgerun.emma.io.Decode;
import io.edgerun.emma.mqtt.ControlPacketType;
import io.edgerun.emma.mqtt.MqttPacket;

/**
 * MqttPacketParser.
 */
public class MqttPacketParser {

    public ControlMessage parse(MqttPacket packet) {
        return parse(packet.getHeader(), packet.getRemLen(), ByteBuffer.wrap(packet.getData()));
    }

    public ControlMessage parse(byte header, int len, ByteBuffer buf) {
        ControlPacketType type = ControlPacketType.fromHeader(header);

        switch (type) {
            case CONNECT:
                return createConnectMessage(len, buf);
            case CONNACK:
                return createConnackMessage(buf);
            case PUBLISH:
                return createPublishMessage(header, len, buf);
            case PUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP:
            case UNSUBACK:
                return createPacketIdentifierMessage(type, buf);
            case SUBSCRIBE:
                return createSubscribeMessage(buf);
            case SUBACK:
                return createSubackMessage(len, buf);
            case UNSUBSCRIBE:
                return createUnsubscribeMessage(buf);
            case PINGREQ:
                return SimpleMessage.PINGREQ;
            case PINGRESP:
                return SimpleMessage.PINGRESP;
            case DISCONNECT:
                return SimpleMessage.DISCONNECT;
            default:
                throw new UnsupportedOperationException("Unhandled message type: " + type);
        }
    }

    private ControlMessage createSubscribeMessage(ByteBuffer buf) {
        int packetId = Decode.readTwoByteInt(buf);

        List<String> topics = new ArrayList<>();
        List<QoS> qos = new ArrayList<>();

        while (buf.hasRemaining()) {
            topics.add(Decode.readLengthEncodedString(buf));
            qos.add(QoS.valueOf(buf.get()));
        }

        return new SubscribeMessage(packetId, topics, qos);
    }

    private ControlMessage createSubackMessage(int len, ByteBuffer buf) {
        int packetId = Decode.readTwoByteInt(buf);

        int n = len - 2;
        QoS[] result = new QoS[n];

        for (int i = 0; i < n; i++) {
            result[i] = QoS.valueOf(buf.get());
        }

        return new SubackMessage(packetId, result);
    }

    private ControlMessage createUnsubscribeMessage(ByteBuffer buf) {
        int packetId = Decode.readTwoByteInt(buf);

        List<String> topics = new ArrayList<>();

        while (buf.hasRemaining()) {
            topics.add(Decode.readLengthEncodedString(buf));
        }

        return new UnsubscribeMessage(packetId, topics);
    }

    private ControlMessage createPacketIdentifierMessage(ControlPacketType type, ByteBuffer buf) {
        return new PacketIdentifierMessage(type, Decode.readTwoByteInt(buf));
    }

    private ControlMessage createConnackMessage(ByteBuffer buf) {
        boolean sessionPresent = buf.get() == 1;
        ConnectReturnCode returnCode = ConnectReturnCode.valueOf(buf.get());

        return new ConnackMessage(sessionPresent, returnCode);
    }

    private ControlMessage createPublishMessage(byte header, int len, ByteBuffer buf) {
        PublishMessage msg = new PublishMessage();

        // parse fixed header flags
        boolean dup = (header & 0b00001000) != 0;
        int qos = ((header >> 1) & 0b00000011);
        boolean retain = (header & 0b00000001) != 0;

        msg.setDup(dup);
        msg.setQos(qos);
        msg.setRetain(retain);

        // parse variable header
        msg.setTopic(Decode.readLengthEncodedString(buf));

        if (qos > 0) {
            msg.setPacketId(Decode.readTwoByteInt(buf));
        }

        if (buf.hasRemaining()) {
            byte[] payload = new byte[buf.remaining()];
            buf.get(payload);
            msg.setPayload(payload);
        } else {
            msg.setPayload(new byte[0]);
        }

        return msg;
    }

    private ControlMessage createConnectMessage(int len, ByteBuffer packet) {
        int endPosition = packet.position() + len;
        ConnectMessage message = new ConnectMessage();

        // variable header
        String protocolName = Decode.readLengthEncodedString(packet);// protocol name

        int protocolLevel = packet.get();
        int connectFlags = packet.get();
        int keepAlive = Decode.readTwoByteInt(packet);

        // payload
        String clientId = Decode.readLengthEncodedString(packet);

        // TODO: everything else is variable depending on the flags that are set
        if (packet.position() != endPosition) {
            packet.position(endPosition);
        }

        // build structured message
        message.setProtocolName(protocolName);
        message.setProtocolLevel(protocolLevel);
        message.setConnectFlags(connectFlags);
        message.setKeepAlive(keepAlive);
        message.setClientId(clientId);

        return message;
    }
}
