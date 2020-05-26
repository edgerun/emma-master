package io.edgerun.emma.mqtt;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Reads a continuous stream of {@link ByteBuffer}s to extract {@link MqttPacket}s from them. This class works like a
 * stateful parser is not thread safe.
 */
public class MqttPacketScanner {

    private final Consumer<MqttPacket> packetConsumer;

    // packet vars
    private byte bHeader;
    private int remLen;
    private ByteBuffer bData;

    // in-flight read variables
    private boolean hasHeader;
    private boolean hasRemLen;
    private int varReadPos;

    public MqttPacketScanner(Consumer<MqttPacket> packetConsumer) {
        this.packetConsumer = packetConsumer;
        reset();
    }

    public void copyState(MqttPacketScanner scanner) {
        bHeader = scanner.bHeader;
        remLen = scanner.remLen;
        bData = scanner.bData;
        hasHeader = scanner.hasHeader;
        hasRemLen = scanner.hasRemLen;
        varReadPos = scanner.varReadPos;
    }

    public void read(ByteBuffer buffer) {
        while (buffer.hasRemaining()) { // make sure to fully drain the buffer
            if (!hasHeader) {
                if (!buffer.hasRemaining()) {
                    return;
                }

                bHeader = buffer.get();
                hasHeader = true;
            }

            if (!hasRemLen) {
                if (!readVariableInt(buffer)) {
                    return;
                }
                hasRemLen = true;

                if (remLen == 0) {
                    onPacketReceive();
                    continue;
                } else {
                    bData = ByteBuffer.allocate(remLen);
                }
            }

            int n = Math.min(bData.remaining(), buffer.remaining());

            for (int i = 0; i < n; i++) {
                bData.put(buffer.get());
            }

            if (!bData.hasRemaining()) {
                onPacketReceive();
            }
        }

    }

    private boolean readVariableInt(ByteBuffer buf) {
        byte b;

        do {
            if (!buf.hasRemaining()) {
                return false;
            }

            b = buf.get();
            remLen += (b & 0b01111111) << (varReadPos * 7);
            varReadPos++;
        } while ((b & 0b10000000) != 0);// check continuation bit

        return true;
    }

    private void onPacketReceive() {
        byte[] data = remLen == 0 ? new byte[0] : bData.array();
        MqttPacket packet = new MqttPacket(bHeader, remLen, data);
        packetConsumer.accept(packet);
        reset();
    }

    private void reset() {
        hasHeader = false;
        hasRemLen = false;
        bHeader = 0;
        bData = null;
        remLen = 0;
        varReadPos = 0;
    }

}
