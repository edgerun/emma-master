package io.edgerun.emma.mqtt.msg;

import io.edgerun.emma.mqtt.ControlPacketType;

/**
 * ConnackMessage.
 */
public class ConnackMessage implements ControlMessage {

    private boolean sessionPresent;
    private ConnectReturnCode returnCode;

    public ConnackMessage() {

    }

    public ConnackMessage(boolean sessionPresent, ConnectReturnCode returnCode) {
        this.sessionPresent = sessionPresent;
        this.returnCode = returnCode;
    }

    public boolean isSessionPresent() {
        return sessionPresent;
    }

    public void setSessionPresent(boolean sessionPresent) {
        this.sessionPresent = sessionPresent;
    }

    public ConnectReturnCode getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(ConnectReturnCode returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.CONNACK;
    }

    @Override
    public String toString() {
        return "CONNACK     {sessionPresent=" + sessionPresent + ", returnCode=" + returnCode + "}";
    }
}
