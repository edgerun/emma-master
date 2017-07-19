package at.ac.tuwien.dsg.emma.mqtt.msg;

import java.util.List;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;
import at.ac.tuwien.dsg.emma.mqtt.QoS;

/**
 * SubscribeMessage.
 */
public class SubscribeMessage implements ControlMessage {

    private final int packetId;
    private final List<String> filter;
    private final List<QoS> requestedQos;

    public SubscribeMessage(int packetId, List<String> filter, List<QoS> requestedQos) {
        this.packetId = packetId;
        this.filter = filter;
        this.requestedQos = requestedQos;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.SUBSCRIBE;
    }

    public int getPacketId() {
        return packetId;
    }

    public List<String> getFilter() {
        return filter;
    }

    public List<QoS> getRequestedQos() {
        return requestedQos;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SUBSCRIBE   {");

        str.append("packetId=").append(packetId).append(", [");

        for (int i = 0; i < filter.size(); i++) {
            if (i > 0) {
                str.append(",");
            }

            str.append(filter.get(i)).append("=").append(requestedQos.get(i));
        }

        str.append("]}");
        return str.toString();
    }
}
