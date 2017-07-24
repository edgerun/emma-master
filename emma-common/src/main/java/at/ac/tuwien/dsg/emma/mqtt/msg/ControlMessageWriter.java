package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import at.ac.tuwien.dsg.emma.mqtt.Encode;

/**
 * ControlMessageWriter.
 */
public class ControlMessageWriter {

    /**
     * Generic method that casts the message type mased on the {@code ControlPacketType}.
     *
     * @param channel the channel to write into
     * @param msg the message to write
     * @throws IOException if an IO error occurs
     */
    public void writeControlMessage(GatheringByteChannel channel, ControlMessage msg) throws IOException {
        switch (msg.getControlPacketType()) {
            case CONNECT:
                write(channel, (ConnectMessage) msg);
                return;
            case CONNACK:
                write(channel, (ConnackMessage) msg);
                return;
            case PUBLISH:
                write(channel, (PublishMessage) msg);
                return;
            case SUBSCRIBE:
                write(channel, (SubscribeMessage) msg);
                return;
            case UNSUBSCRIBE:
                write(channel, (UnsubscribeMessage) msg);
                return;
            case SUBACK:
                write(channel, (SubackMessage) msg);
                return;
            case PINGREQ:
            case PINGRESP:
            case DISCONNECT:
                write(channel, (SimpleMessage) msg);
                return;
            case PUBACK:
            case PUBREC:
            case PUBREL:
            case PUBCOMP:
            case UNSUBACK:
                write(channel, (PacketIdentifierMessage) msg);
                return;
            default:
                throw new IllegalArgumentException("Unhandled packet type " + msg.getControlPacketType());
        }
    }

    public void write(GatheringByteChannel channel, ConnectMessage msg) throws IOException {
        ByteBuffer head = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(1024 * 6); // FIXME

        put(head, data, msg);

        write(channel, head, data);
    }

    public void write(GatheringByteChannel channel, PublishMessage msg) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(msg.getPayload().length + msg.getTopic().length() + 4);

        put(header, data, msg);

        write(channel, header, data);
    }

    public void write(WritableByteChannel channel, PacketIdentifierMessage msg) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);

        put(buf, msg);

        write(channel, buf);
    }

    public void write(WritableByteChannel channel, ConnackMessage msg) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);

        Encode.writeHeader(buf, msg.getControlPacketType());
        buf.put((byte) 2); // rem len
        buf.put((byte) (msg.isSessionPresent() ? 1 : 0));
        buf.put((byte) msg.getReturnCode().ordinal());

        write(channel, buf);
    }

    public void write(WritableByteChannel channel, SimpleMessage msg) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);

        buf.put(msg.getControlPacketType().toHeader());
        buf.put((byte) 0);

        write(channel, buf);
    }

    public void write(GatheringByteChannel channel, SubscribeMessage msg) throws IOException {
        ByteBuffer head = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(1024 * 6); // FIXME

        put(head, data, msg);

        write(channel, head, data);
    }

    public void write(GatheringByteChannel channel, SubackMessage msg) throws IOException {
        ByteBuffer head = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(2 + msg.getFilterQos().length); // packet id + return codes

        put(head, data, msg);

        write(channel, head, data);
    }

    public void write(GatheringByteChannel channel, UnsubscribeMessage msg) throws IOException {
        ByteBuffer head = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(1024 * 6); // FIXME

        put(head, data, msg);

        write(channel, head, data);
    }

    private void write(WritableByteChannel channel, ByteBuffer buf) throws IOException {
        buf.flip();
        channel.write(buf);
    }

    private void write(GatheringByteChannel channel, ByteBuffer head, ByteBuffer data) throws IOException {
        head.flip();
        data.flip();
        channel.write(new ByteBuffer[]{head, data});
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
