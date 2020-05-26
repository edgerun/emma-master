package io.edgerun.emma.monitoring;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.Queue;

import io.edgerun.emma.io.DatagramLoop;
import io.edgerun.emma.monitoring.msg.MonitoringMessage;
import io.edgerun.emma.monitoring.msg.MonitoringMessageReader;
import io.edgerun.emma.monitoring.msg.MonitoringMessageWriter;
import io.edgerun.emma.monitoring.msg.PingMessage;
import io.edgerun.emma.monitoring.msg.PingReqMessage;
import io.edgerun.emma.monitoring.msg.PingRespMessage;
import io.edgerun.emma.monitoring.msg.PongMessage;
import io.edgerun.emma.monitoring.msg.ReconnectAck;
import io.edgerun.emma.monitoring.msg.ReconnectRequest;
import io.edgerun.emma.monitoring.msg.UsageRequest;
import io.edgerun.emma.monitoring.msg.UsageResponse;

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

        if (element == null) {
            return;
        }

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
            case MonitoringPacketType.PING:
                handler.onMessage(this, (PingMessage) message);
                return;
            case MonitoringPacketType.PONG:
                handler.onMessage(this, (PongMessage) message);
                return;
            case MonitoringPacketType.PINGREQ:
                handler.onMessage(this, (PingReqMessage) message);
                return;
            case MonitoringPacketType.PINGRESP:
                handler.onMessage(this, (PingRespMessage) message);
                return;
            case MonitoringPacketType.USAGEREQ:
                handler.onMessage(this, (UsageRequest) message);
                break;
            case MonitoringPacketType.USAGERESP:
                handler.onMessage(this, (UsageResponse) message);
                break;
            case MonitoringPacketType.RECONNREQ:
                handler.onMessage(this, (ReconnectRequest) message);
                return;
            case MonitoringPacketType.RECONNACK:
                handler.onMessage(this, (ReconnectAck) message);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

}
