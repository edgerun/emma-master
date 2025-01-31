package io.edgerun.emma.mqtt.msg;

import java.nio.channels.SocketChannel;

/**
 * MessageEnvelope.
 */
public class MessageEnvelope {

    private SocketChannel origin;
    private SocketChannel destination;
    private ControlMessage message;
    private boolean multicast;

    public MessageEnvelope(ControlMessage message) {
        this.message = message;
    }

    public void setMulticast(boolean multicast) {
        this.multicast = multicast;
    }

    public boolean isMulticast() {
        return multicast;
    }

    public SocketChannel getOrigin() {
        return origin;
    }

    public MessageEnvelope setOrigin(SocketChannel origin) {
        this.origin = origin;
        return this;
    }

    public SocketChannel getDestination() {
        return destination;
    }

    public MessageEnvelope setDestination(SocketChannel destination) {
        this.destination = destination;
        return this;
    }

    public ControlMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message[" + String.valueOf(message) + " " + String.valueOf(origin) + " -> " + String.valueOf(destination) + "]";
    }
}
