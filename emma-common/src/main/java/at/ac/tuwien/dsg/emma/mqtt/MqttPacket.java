package at.ac.tuwien.dsg.emma.mqtt;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * MqttPacket.
 */
public class MqttPacket {
    private final byte header;
    private final int remLen;
    private final byte[] data;

    public MqttPacket(byte header, int remLen, byte[] data) {
        this.header = header;
        this.remLen = remLen;
        this.data = data;
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

        return packet;
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
