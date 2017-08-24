package at.ac.tuwien.dsg.emma.monitoring;

import java.io.IOException;
import java.net.InetSocketAddress;
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
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.ReconnectMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageRequest;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageResponse;

/**
 * MonitoringLoop.
 */
public class MonitoringLoop extends DatagramLoop {

    private MonitoringMessageHandler inHandler;
    private MonitoringMessageHandler outHandler;

    private Queue<MonitoringMessage> outQueue;

    private MonitoringMessageReader reader;
    private MonitoringMessageWriter writer;

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;

    public MonitoringLoop(InetSocketAddress bind) {
        super(bind);

        this.outQueue = new ArrayDeque<>();

        this.reader = new MonitoringMessageReader();
        this.writer = new MonitoringMessageWriter();

        this.readBuffer = ByteBuffer.allocate(64);
        this.writeBuffer = ByteBuffer.allocate(64);
    }

    public MonitoringMessageHandler getReadHandler() {
        return inHandler;
    }

    public MonitoringLoop setReadHandler(MonitoringMessageHandler handler) {
        this.inHandler = handler;
        return this;
    }

    public MonitoringMessageHandler getWriteHandler() {
        return outHandler;
    }

    public MonitoringLoop setWriteHandler(MonitoringMessageHandler handler) {
        this.outHandler = handler;
        return this;
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
        if (inHandler != null) {
            callHandler(inHandler, message);
        }
    }

    private void onBeforeWrite(MonitoringMessage message) {
        if (outHandler != null) {
            callHandler(outHandler, message);
        }
    }

    private void doWrite(DatagramChannel channel) throws IOException {
        MonitoringMessage element = outQueue.poll();

        onBeforeWrite(element);
        // write message into buffer
        writeBuffer.clear();
        writer.write(writeBuffer, element);
        writeBuffer.flip();

        // send message
        channel.send(writeBuffer, element.getDestination());
    }

    private void doRead(DatagramChannel channel) throws IOException {
        readBuffer.clear();
        InetSocketAddress address = (InetSocketAddress) channel.receive(readBuffer);
        readBuffer.flip();
        MonitoringMessage message = reader.read(readBuffer);
        message.setSource(address);

        onMessageReceived(message);
    }

    private void callHandler(MonitoringMessageHandler handler, MonitoringMessage message) {
        switch (message.getMonitoringPacketType()) {
            case PING:
                handler.onMessage(this, (PingMessage) message);
                return;
            case PONG:
                handler.onMessage(this, (PongMessage) message);
                return;
            case PINGREQ:
                handler.onMessage(this, (PingReqMessage) message);
                return;
            case PINGRESP:
                handler.onMessage(this, (PingRespMessage) message);
                return;
            case USAGEREQ:
                handler.onMessage(this, (UsageRequest) message);
                break;
            case USAGERESP:
                handler.onMessage(this, (UsageResponse) message);
                break;
            case RECONNECT:
                handler.onMessage(this, (ReconnectMessage) message);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
