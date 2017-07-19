package at.ac.tuwien.dsg.emma.mqtt.msg;

import at.ac.tuwien.dsg.emma.mqtt.ControlPacketType;

/**
 * PacketIdentifierMessage.
 */
public class PacketIdentifierMessage extends SimpleMessage {

    private final int packetIdentifier;

    public PacketIdentifierMessage(ControlPacketType controlPacketType, int packetIdentifier) {
        super(controlPacketType);
        this.packetIdentifier = packetIdentifier;
    }

    public int getPacketIdentifier() {
        return packetIdentifier;
    }

    @Override
    public String toString() {
        return String.format("%-11s {packetId=%d}", getControlPacketType(), packetIdentifier);
    }
}
