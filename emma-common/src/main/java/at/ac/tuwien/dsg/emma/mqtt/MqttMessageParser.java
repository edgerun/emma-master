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
