package io.edgerun.emma.mqtt.msg;

import io.edgerun.emma.mqtt.ControlPacketType;

/**
 * PublishMessage.
 */
public class PublishMessage implements ControlMessage {

    // byte 1 header
    private boolean retain;
    private boolean dup;
    private int qos;

    // var header
    private String topic;
    private int packetId;

    // payload
    private byte[] payload;

    public PublishMessage() {

    }

    public PublishMessage(String topic, byte[] payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    @Override
    public ControlPacketType getControlPacketType() {
        return ControlPacketType.PUBLISH;
    }

    public PacketIdentifierMessage createPuback() {
        return new PacketIdentifierMessage(ControlPacketType.PUBACK, getPacketId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PUBLISH     {");
        sb.append("retain=").append(retain);
        sb.append(", dup=").append(dup);
        sb.append(", qos=").append(qos);
        sb.append(", topic='").append(topic).append('\'');
        sb.append(", packetId=").append(packetId);
        sb.append(", payload='").append(new String(payload)).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
