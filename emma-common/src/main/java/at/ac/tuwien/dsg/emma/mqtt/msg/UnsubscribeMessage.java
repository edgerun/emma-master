package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.util.List;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;

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

    @Override
    public String toString() {
        return "UNSUBSCRIBE {" + "packetId=" + packetId + ", topics=" + topics + '}';
    }
}
