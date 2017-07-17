package at.ac.tuwien.dsg.emma.mqtt;

/**
 * ControllMessages.
 */
public final class ControllMessages {
    private ControllMessages() {
        // util class
    }

    public static final MqttPacket DISCONNECT = new MqttPacket(ControlPacketType.DISCONNECT.toHeader());
    public static final MqttPacket PINGREQ = new MqttPacket(ControlPacketType.PINGREQ.toHeader());
    public static final MqttPacket PINGRESP = new MqttPacket(ControlPacketType.PINGRESP.toHeader());
}
