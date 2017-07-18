package at.ac.tuwien.dsg.emma.mqtt;

import java.nio.ByteBuffer;

/**
 * MqttMessageParser.
 */
public class MqttMessageParser {

    public ControlMessage parse(MqttPacket packet) {
        return parse(packet.getHeader(), packet.getRemLen(), ByteBuffer.wrap(packet.getData()));
    }

    public ControlMessage parse(byte header, int len, ByteBuffer buf) {
        switch (ControlPacketType.fromHeader(header)) {
            case CONNECT:
                return createConnectMessage(len, buf);
            case CONNACK:
                return createConnackMessage(len, buf);
            case DISCONNECT:
                return createDisconnectMessage(len, buf);
            case PUBLISH:
                return createPublishMessage(header, len, buf);
            default:
                throw new RuntimeException();
        }
    }

    private ControlMessage createDisconnectMessage(int len, ByteBuffer buf) {
        return null;
    }

    private ControlMessage createConnackMessage(int len, ByteBuffer buf) {
        return null;
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
