package at.ac.tuwien.dsg.emma.mqtt.msg;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;

/**
 * SimpleMessage.
 */
public class SimpleMessage implements ControlMessage {

    public static final ControlMessage PINGREQ = new SimpleMessage(ControlPacketType.PINGREQ);
    public static final ControlMessage PINGRESP = new SimpleMessage(ControlPacketType.PINGRESP);
    public static final ControlMessage DISCONNECT = new SimpleMessage(ControlPacketType.DISCONNECT);

    private final ControlPacketType controlPacketType;

    SimpleMessage(ControlPacketType controlPacketType) {
        this.controlPacketType = controlPacketType;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return controlPacketType;
    }

    @Override
    public String toString() {
        return controlPacketType.toString();
    }
}
