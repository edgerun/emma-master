package io.edgerun.emma.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Encapsulates data necessary to send a packet via a {@link java.nio.channels.DatagramChannel}.
 */
public class DatagramPacket {

    private ByteBuffer buffer;
    private SocketAddress destination;

    public DatagramPacket(ByteBuffer buffer, SocketAddress destination) {
        this.buffer = buffer;
        this.destination = destination;
    }

    public int send(DatagramChannel channel) throws IOException {
        if (buffer != null && destination != null) {
            return channel.send(buffer, destination);
        } else {
            throw new IllegalStateException("Buffer or destination not set");
        }
    }

    @Override
    public String toString() {
        return "DatagramPacket{" +
                "buffer=" + buffer +
                ", destination=" + destination +
                '}';
    }
}
