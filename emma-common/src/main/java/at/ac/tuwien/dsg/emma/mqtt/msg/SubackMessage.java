package at.ac.tuwien.dsg.emma.mqtt.msg;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;

/**
 * SubackMessage.
 */
public class SubackMessage implements ControlMessage {

    private final int packetId;
    private final QoS[] filterQos;

    public SubackMessage(int packetId, QoS[] filterQos) {
        this.packetId = packetId;
        this.filterQos = filterQos;
    }

    public int getPacketId() {
        return packetId;
    }

    public QoS[] getFilterQos() {
        return filterQos;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.SUBACK;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SUBACK      {");

        str.append("packetId=").append(packetId).append(", subscriptions=[");

        for (int i = 0; i < filterQos.length; i++) {
            if (i > 0) {
                str.append(",");
            }

            QoS qos = filterQos[i];
            str.append(i).append("=").append(qos == null ? "fail" : qos.toString());
        }
        str.append("]}");

        return str.toString();
    }
}
