package at.ac.tuwien.dsg.emma.monitoring;

/**
 * MonitoringPacketType.
 */
public enum MonitoringPacketType {
    UNKNOWN,
    PINGREQ,
    PINGRESP,
    PING,
    PONG,
    USAGEREQ,
    USAGERESP;

    private static final MonitoringPacketType[] values = values();

    public static MonitoringPacketType fromHeader(byte header) {
        int i = header & 0xFF;
        if (i >= values.length) {
            return UNKNOWN;
        }

        return values[header];
    }

    public byte toHeader() {
        return (byte) (ordinal() & 0xFF);
    }

}
