package io.edgerun.emma.mqtt.msg;

import java.util.List;

import io.edgerun.emma.mqtt.ControlPacketType;

/**
 * UnsubscribeMessage.
 */
public class UnsubscribeMessage implements ControlMessage {

    private final int packetId;
    private final List<String> topics;

    public UnsubscribeMessage(int packetId, List<String> topics) {
        this.packetId = packetId;
        this.topics = topics;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.UNSUBSCRIBE;
    }

    public int getPacketId() {
        return packetId;
    }

    public List<String> getTopics() {
        return topics;
    }

    @Override
    public String toString() {
        return "UNSUBSCRIBE {" + "packetId=" + packetId + ", topics=" + topics + '}';
    }
}
