package at.ac.tuwien.dsg.emma.mqtt.msg;

/**
 * ConnectReturnCode.
 */
public enum ConnectReturnCode {
    ACCEPTED, // 0x00
    UNACCEPTABLE_PROTOCOL_VERSION, // 0x01
    IDENTIFIER_REJECTED, // 0x02
    SERVICE_UNAVAILABLE, // 0x03
    BAD_USERNAME_OR_PASSWORD, // 0x04
    NOT_AUTHORIZED; // 0x05

    private static final ConnectReturnCode[] values = values();

    public static ConnectReturnCode valueOf(byte header) {
        return values[header];
    }

}
