package at.ac.tuwien.dsg.emma.mqtt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Objects;

import at.ac.tuwien.dsg.emma.io.Encode;
import at.ac.tuwien.dsg.emma.io.ThreadLocalBuffer;

/**
 * MqttPacketWriter.
 */
public class MqttPacketWriter {

    private static ThreadLocalBuffer header = ThreadLocalBuffer.create(8);

    public long write(GatheringByteChannel channel, MqttPacket packet) throws IOException {
        Objects.requireNonNull(channel);

        ByteBuffer hBuf = header.getClean();
        hBuf.put(packet.getHeader());
        Encode.writeVariableInt(hBuf, packet.getRemLen());
        hBuf.flip();

        byte[] data = packet.getData();

        if (data != null && data.length > 0) {
            return channel.write(new ByteBuffer[]{hBuf, ByteBuffer.wrap(data)});
        } else {
            return channel.write(hBuf);
        }

    }
}
