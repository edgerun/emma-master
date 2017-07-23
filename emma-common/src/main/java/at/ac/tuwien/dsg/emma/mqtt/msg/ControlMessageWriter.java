package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;

import at.ac.tuwien.dsg.emma.mqtt.Encode;
import at.ac.tuwien.dsg.emma.mqtt.QoS;

/**
 * ControlMessageWriter.
 */
public class ControlMessageWriter {

    public void write(GatheringByteChannel channel, ConnectMessage msg) throws IOException {
        ByteBuffer head = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(1024 * 6); // FIXME

        put(head, data, msg);

        head.flip();
        data.flip();

        channel.write(new ByteBuffer[]{head, data});
    }

    public void write(GatheringByteChannel channel, PublishMessage msg) throws IOException {
        ByteBuffer header = ByteBuffer.allocate(5);
        ByteBuffer data = ByteBuffer.allocate(msg.getPayload().length + msg.getTopic().length() + 4);

        put(header, data, msg);

        header.flip();
        data.flip();

        channel.write(new ByteBuffer[]{header, data});
    }

    public void write(WritableByteChannel channel, PacketIdentifierMessage msg) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        put(buf, msg);
        buf.flip();
        channel.write(buf);
    }

    public void write(WritableByteChannel channel, SimpleMessage msg) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.put(msg.getControlPacketType().toHeader());
        buf.put((byte) 0);
        buf.flip();
        channel.write(buf);
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
            // TODO will message
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

    public void put(ByteBuffer buf, PacketIdentifierMessage message) {
        buf.put(message.getControlPacketType().toHeader());
        buf.put((byte) 2);
        Encode.writeTwoByteInt(buf, message.getPacketIdentifier());
    }

    public void put(ByteBuffer header, ByteBuffer buf, SubscribeMessage message) {
        // DATA
        int pos = buf.position();
        List<String> topics = message.getFilter();
        List<QoS> qos = message.getRequestedQos();

        for (int i = 0; i < topics.size(); i++) {
            Encode.writeLengthEncodedString(buf, topics.get(i));
            buf.put((byte) (qos.get(i).ordinal() & 0b00000011));
        }

        // HEADER
        int len = buf.position() - pos;
        header.put(message.getControlPacketType().toHeader());
        Encode.writeVariableInt(header, len);
    }

    private void put(ByteBuffer header, ByteBuffer data, PublishMessage message) {
        int pos = data.position();

        // variable header
        Encode.writeLengthEncodedString(data, message.getTopic());

        if (message.getQos() > 0) {
            Encode.writeTwoByteInt(data, message.getPacketId());
        }

        // payload
        data.put(message.getPayload());

        // header
        int retain = message.isRetain() ? 1 : 0;
        int qos = message.getQos();
        int dup = (message.isDup() ? 1 : 0);
        byte flags = (byte) ((retain | (qos << 1) | dup << 3) & 0x0F);

        header.put(message.getControlPacketType().toHeader(flags));

        // rem len
        int len = data.position() - pos;
        Encode.writeVariableInt(header, len);
    }

}
