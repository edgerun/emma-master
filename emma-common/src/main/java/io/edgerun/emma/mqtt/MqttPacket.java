package io.edgerun.emma.mqtt;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

import io.edgerun.emma.io.Decode;
import io.edgerun.emma.io.Encode;

/**
 * MqttPacket.
 */
public class MqttPacket {

    private final byte header;
    private final int remLen;
    private final byte[] data;

    private SocketChannel origin;
    private SocketChannel destination;

    public MqttPacket(byte header) {
        this(header, 0, new byte[0]);
    }

    public MqttPacket(byte header, int remLen, byte[] data) {
        this.header = header;
        this.remLen = remLen;
        this.data = data;
    }

    public MqttPacket(ByteBuffer packet) {
        header = packet.get();
        remLen = Decode.readVariableInt(packet);
        data = new byte[remLen];
        packet.get(data);
    }

    public ControlPacketType getType() {
        return ControlPacketType.fromHeader(header);
    }

    public byte getHeader() {
        return header;
    }

    public int getRemLen() {
        return remLen;
    }

    public byte[] getData() {
        return data;
    }

    public ByteBuffer asBuffer() {
        ByteBuffer bRemLen = ByteBuffer.allocate(4);
        Encode.writeVariableInt(bRemLen, remLen);
        bRemLen.flip();

        ByteBuffer packet = ByteBuffer.allocate(1 + bRemLen.remaining() + data.length);

        packet.put(header);
        packet.put(bRemLen);
        packet.put(data);

        packet.flip();
        return packet;
    }

    public SocketChannel getOrigin() {
        return origin;
    }

    public void setOrigin(SocketChannel origin) {
        this.origin = origin;
    }

    public SocketChannel getDestination() {
        return destination;
    }

    public void setDestination(SocketChannel destination) {
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "MqttPacket{" +
                "type = " + ControlPacketType.fromHeader(header) +
                ", header=" + header +
                ", remLen=" + remLen +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
