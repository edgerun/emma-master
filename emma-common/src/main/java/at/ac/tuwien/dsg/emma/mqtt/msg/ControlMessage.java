package at.ac.tuwien.dsg.emma.mqtt.msg;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;

/**
 * ControlMessage.
 */
public interface ControlMessage {
    // tagging interface
    ControlPacketType getControlPacketType();
}
