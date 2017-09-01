package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import at.ac.tuwien.dsg.emma.io.Encode;
import at.ac.tuwien.dsg.emma.io.ThreadLocalBuffer;

/**
 * ControlMessageWriter.
 */
public class ControlMessageWriter {

    private static ThreadLocalBuffer smallBuffer = ThreadLocalBuffer.create(8);
    private static ThreadLocalBuffer dataBuffer = ThreadLocalBuffer.create(1024 * 10); // FIXME

    /**
     * Generic method that casts the message type based on the {@code ControlPacketType}.
     *
     * @param channel the channel to write into
     * @param msg the message to write
     * @throws IOException if an IO error occurs
     */
    public long writeControlMessage(GatheringByteChannel channel, ControlMessage msg) throws IOException {
        switch (msg.getControlPacketType()) {
            case CONNECT:
                return write(channel, (ConnectMessage) msg);
            case CONNACK:
                return write(channel, (ConnackMessage) msg);
            case PUBLISH:
                return write(channel, (PublishMessage) msg);
            case SUBSCRIBE:
                return write(channel, (SubscribeMessage) msg);
            case UNSUBSCRIBE:
                return write(channel, (UnsubscribeMessage) msg);
            case SUBACK:
                return write(channel, (SubackMessage) msg);
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                return write(channel, (SimpleMessage) msg);
            case PUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP:
            case UNSUBACK:
                return write(channel, (PacketIdentifierMessage) msg);
            default:
                throw new IllegalArgumentException("Unhandled packet type " + msg.getControlPacketType());
        }
    }

    public long write(GatheringByteChannel channel, ConnectMessage msg) throws IOException {
        ByteBuffer head = smallBuffer.getClean();
        ByteBuffer data = dataBuffer.getClean();

        put(head, data, msg);

        return write(channel, head, data);
    }

    public long write(GatheringByteChannel channel, PublishMessage msg) throws IOException {
        ByteBuffer header = smallBuffer.getClean();
        ByteBuffer data = dataBuffer.getClean(); // required len = msg.getPayload().length + msg.getTopic().length() + 4

        put(header, data, msg);

        return write(channel, header, data);
    }

    public long write(WritableByteChannel channel, PacketIdentifierMessage msg) throws IOException {
        ByteBuffer buf = smallBuffer.getClean();

        put(buf, msg);

        return write(channel, buf);
    }

    public long write(WritableByteChannel channel, ConnackMessage msg) throws IOException {
        ByteBuffer buf = smallBuffer.getClean();

        buf.put(msg.getControlPacketType().toHeader());
        buf.put((byte) 2); // rem len
        buf.put((byte) (msg.isSessionPresent() ? 1 : 0));
        buf.put((byte) msg.getReturnCode().ordinal());

        return write(channel, buf);
    }

    public long write(WritableByteChannel channel, SimpleMessage msg) throws IOException {
        ByteBuffer buf = smallBuffer.getClean();

        buf.put(msg.getControlPacketType().toHeader());
        buf.put((byte) 0);

        return write(channel, buf);
    }

    public long write(GatheringByteChannel channel, SubscribeMessage msg) throws IOException {
        ByteBuffer head = smallBuffer.getClean();
        ByteBuffer data = dataBuffer.getClean(); // FIXME

        put(head, data, msg);

        return write(channel, head, data);
    }

    public long write(GatheringByteChannel channel, SubackMessage msg) throws IOException {
        ByteBuffer head = smallBuffer.getClean();
        ByteBuffer data = dataBuffer.getClean(); // packet id + return codes 2 + msg.getFilterQos().length

        put(head, data, msg);

        return write(channel, head, data);
    }

    public long write(GatheringByteChannel channel, UnsubscribeMessage msg) throws IOException {
        ByteBuffer head = smallBuffer.getClean();
        ByteBuffer data = dataBuffer.getClean(); // FIXME

        put(head, data, msg);

        return write(channel, head, data);
    }

    private long write(WritableByteChannel channel, ByteBuffer buf) throws IOException {
        buf.flip();
        return channel.write(buf);
    }

    private long write(GatheringByteChannel channel, ByteBuffer head, ByteBuffer data) throws IOException {
        head.flip();
        data.flip();

        long expected = head.remaining() + data.remaining();
        long actual = channel.write(new ByteBuffer[]{head, data});

        if(expected != actual) {
            System.err.printf("Error writing packet to %s: expected = %d, actual = %d", channel, expected, actual);
        }

        return actual;
    }

    // TODO

    public void put(ByteBuffer header, ByteBuffer buf, ConnectMessage msg) {
        int pos = buf.position();
        // variable header
        Encode.writeLengthEncodedString(buf, msg.getProtocolName());
        Encode.writeOneByteInt(buf, msg.getProtocolLevel());
        Encode.writeOneByteInt(buf, msg.getConnectFlags());
        Encode.writeTwoByteInt(buf, msg.getKeepAlive());

        // payload
        Encode.writeLengthEncodedString(buf, msg.getClientId());

        if (msg.hasWill()) {
            throw new UnsupportedOperationException("Cannot write will messages yet");
            // TODO will topic
            // TODO will msg
        }
        if (msg.hasUserName()) {
            Encode.writeLengthEncodedString(buf, msg.getUserName());
        }
        if (msg.hasPassword()) {
            throw new UnsupportedOperationException("Can't write passwords yet");
        }

        int len = buf.position() - pos;
        header.put(msg.getControlPacketType().toHeader());
        Encode.writeVariableInt(header, len);
    }

    public void put(ByteBuffer buf, PacketIdentifierMessage msg) {
        buf.put(msg.getControlPacketType().toHeader());
        buf.put((byte) 2);
        Encode.writeTwoByteInt(buf, msg.getPacketIdentifier());
    }

    public void put(ByteBuffer header, ByteBuffer buf, SubscribeMessage msg) {
        int pos = buf.position();

        // variable header
        Encode.writeTwoByteInt(buf, msg.getPacketId());

        // payload
        List<String> topics = msg.getFilter();
        List<QoS> qos = msg.getRequestedQos();

        for (int i = 0; i < topics.size(); i++) {
            Encode.writeLengthEncodedString(buf, topics.get(i));
            buf.put((byte) (qos.get(i).ordinal() & 0b00000011));
        }

        // header
        int len = buf.position() - pos;
        header.put(msg.getControlPacketType().toHeader());
        Encode.writeVariableInt(header, len);
    }

    private void put(ByteBuffer header, ByteBuffer data, SubackMessage msg) {
        int pos = data.position();

        // variable header
        Encode.writeTwoByteInt(data, msg.getPacketId());

        // payload
        for (QoS qos : msg.getFilterQos()) {
            if (qos != null) {
                Encode.writeOneByteInt(data, qos.ordinal());
            } else {
                Encode.writeOneByteInt(data, 0x80);
            }
        }

        // header
        int len = data.position() - pos;
        header.put(msg.getControlPacketType().toHeader());
        Encode.writeVariableInt(header, len);
    }

    private void put(ByteBuffer header, ByteBuffer data, UnsubscribeMessage msg) {
        int pos = data.position();

        // variable header
        Encode.writeTwoByteInt(data, msg.getPacketId());

        // payload
        for (String topic : msg.getTopics()) {
            Encode.writeLengthEncodedString(data, topic);
        }

        // header
        int len = data.position() - pos;
        header.put(msg.getControlPacketType().toHeader(0b0010));
        Encode.writeVariableInt(header, len);
    }

    private void put(ByteBuffer header, ByteBuffer data, PublishMessage msg) {
        int pos = data.position();

        // variable header
        Encode.writeLengthEncodedString(data, msg.getTopic());

        if (msg.getQos() > 0) {
            Encode.writeTwoByteInt(data, msg.getPacketId());
        }

        // payload
        data.put(msg.getPayload());

        // header
        int retain = msg.isRetain() ? 1 : 0;
        int qos = msg.getQos();
        int dup = (msg.isDup() ? 1 : 0);
        byte flags = (byte) ((retain | (qos << 1) | dup << 3) & 0x0F);

        header.put(msg.getControlPacketType().toHeader(flags));

        // rem len
        int len = data.position() - pos;
        Encode.writeVariableInt(header, len);
    }

}
