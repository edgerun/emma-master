package io.edgerun.emma.mqtt;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.Objects;

import io.edgerun.emma.io.Encode;
import io.edgerun.emma.io.ThreadLocalBuffer;
import io.edgerun.emma.util.IOUtils;

/**
 * MqttPacketWriter.
 */
public class MqttPacketWriter {

    private static ThreadLocalBuffer header = ThreadLocalBuffer.create(8);
    private static ThreadLocal<ByteBuffer> remaining = new ThreadLocal<>();

    public boolean write(GatheringByteChannel channel, MqttPacket packet) throws IOException {
        Objects.requireNonNull(channel);

        ByteBuffer hBuf = header.getClean();
        hBuf.put(packet.getHeader());
        Encode.writeVariableInt(hBuf, packet.getRemLen());
        hBuf.flip();

        byte[] data = packet.getData();

        long expected = hBuf.remaining();
        long actual = 0;
        ByteBuffer remBuf = null;

        if (data != null && data.length > 0) {
            expected += data.length;
            ByteBuffer[] buffers = {hBuf, ByteBuffer.wrap(data)};
            actual = channel.write(buffers);
            remBuf = IOUtils.copyRemaining(buffers);
        } else {
            actual = channel.write(hBuf);
            remBuf = IOUtils.copyRemaining(hBuf);
        }

        if (expected != actual) {
            remaining.set(remBuf);
            return false;
        } else {
            return true;
        }
    }

    public ByteBuffer getRemaining() {
        return remaining.get();
    }
}
