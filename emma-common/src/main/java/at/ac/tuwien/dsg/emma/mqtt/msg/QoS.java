package at.ac.tuwien.dsg.emma.mqtt.msg;

/**
 * QoS.
 */
public enum QoS {
    // QoS = 0x00
    AT_MOST_ONCE,

    // QoS = 0x01
    AT_LEAST_ONCE,

    // QoS = 0x02
    EXACTLY_ONCE;

    public static QoS valueOf(byte b) {
        if (b == 0x00) {
            return QoS.AT_MOST_ONCE;
        } else if (b == 0x01) {
            return QoS.AT_LEAST_ONCE;
        } else if (b == 0x02) {
            return QoS.EXACTLY_ONCE;
        }

        return null;
    }
}
