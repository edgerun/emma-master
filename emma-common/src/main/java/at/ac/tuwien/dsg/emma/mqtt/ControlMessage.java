package at.ac.tuwien.dsg.emma.mqtt;

/**
 * ControlMessage.
 */
public interface ControlMessage {
    // tagging interface
    ControlPacketType getControlPacketType();
}
