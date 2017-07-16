package at.ac.tuwien.dsg.emma.mqtt;

/**
 * ControlPacketType.
 */
public enum ControlPacketType {
    RESERVED_0,
    CONNECT,
    CONNACK,
    PUBLISH,
    PUBACK,
    PUBREC,
    PUBREL,
    PUBCOMP,
    SUBSCRIBE,
    SUBACK,
    UNSUBSCRIBE,
    UNSUBACK,
    PINGREQ,
    PINGRESP,
    DISCONNECT,
    RESERVED_15;

    private static final ControlPacketType[] values = values();

    public static ControlPacketType fromHeader(byte header) {
        int ord = (header >> 4) & 0b00001111;
        return values[ord];
    }

    public byte toHeader() {
        return (byte) ((ordinal() << 4) & 0b11110000);
    }

    public byte toHeader(int flags) {
        int type = (ordinal() << 4) & 0b11110000;
        return (byte) ((type | flags) & 0b11111111);
    }
}
