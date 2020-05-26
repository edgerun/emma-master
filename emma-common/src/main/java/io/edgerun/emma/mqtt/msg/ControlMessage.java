package io.edgerun.emma.mqtt.msg;

import io.edgerun.emma.mqtt.ControlPacketType;

/**
 * ControlMessage.
 */
public interface ControlMessage {
    // tagging interface
    ControlPacketType getControlPacketType();
}
