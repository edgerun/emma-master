package at.ac.tuwien.dsg.emma.monitoring;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.Queue;

import at.ac.tuwien.dsg.emma.io.DatagramLoop;
import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessageReader;
import at.ac.tuwien.dsg.emma.monitoring.msg.MonitoringMessageWriter;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;

/**
 * MonitoringLoop.
 */
public class MonitoringLoop extends DatagramLoop {

    private MonitoringMessageHandler handler;

    private Queue<MonitoringMessage> outQueue;

    private MonitoringMessageReader reader;
    private MonitoringMessageWriter writer;

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    public MonitoringLoop(InetSocketAddress bind) {
        this(bind, null);
    }

    public MonitoringLoop(InetSocketAddress bind, MonitoringMessageHandler handler) {
        super(bind);
        this.handler = handler;

        this.outQueue = new ArrayDeque<>();

        this.reader = new MonitoringMessageReader();
        this.writer = new MonitoringMessageWriter();

        this.readBuffer = ByteBuffer.allocate(512);
        this.writeBuffer = ByteBuffer.allocate(512);
    }

    public MonitoringMessageHandler getHandler() {
        return handler;
    }

    public void setHandler(MonitoringMessageHandler handler) {
        this.handler = handler;
    }

    public void send(MonitoringMessage e) {
        if (key == null) {
            throw new IllegalStateException("Not started yet");
        }
        if (e.getDestination() == null) {
            throw new IllegalArgumentException("Message has no destination");
        }

        queue(e);
        key.selector().wakeup();
    }

    public void queue(MonitoringMessage message) {
        outQueue.add(message);
        key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    @Override
    public void handle(DatagramChannel channel, SelectionKey key) throws IOException {
        if (key.isReadable()) {
            doRead(channel);
        }

        if (key.isWritable()) {
            if (outQueue.isEmpty()) {
                key.interestOps(SelectionKey.OP_READ);
            } else {
                doWrite(channel);
            }
        }
    }

    /**
     * Calls the message handler.
     *
     * @param message the message being received
     */
    protected void onMessageReceived(MonitoringMessage message) {
        if (handler == null) {
            return;
        }

        switch (message.getMonitoringPacketType()) {
            case PING:
                handler.onMessage(this, (PingMessage) message);
                return;
            case PONG:
                handler.onMessage(this, (PongMessage) message);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void doWrite(DatagramChannel channel) throws IOException {
        MonitoringMessage element = outQueue.poll();

        // write message into buffer
        writeBuffer.clear();
        writer.write(writeBuffer, element);
        writeBuffer.flip();

        // send message
        channel.send(writeBuffer, element.getDestination());
    }

    private void doRead(DatagramChannel channel) throws IOException {
        readBuffer.clear();
        SocketAddress address = channel.receive(readBuffer);
        readBuffer.flip();
        MonitoringMessage message = reader.read(readBuffer);
        message.setSource(address);

        onMessageReceived(message);
    }

}
