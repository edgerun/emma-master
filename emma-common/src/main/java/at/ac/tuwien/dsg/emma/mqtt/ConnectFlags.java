package at.ac.tuwien.dsg.emma.mqtt;

/**
 * ConnectFlags.
 */
public class ConnectFlags {

    public static final int USER_NAME = 0b10000000;
    public static final int PASSWORD_FLAG = 0b01000000;
    public static final int WILL_RETAIN = 0b00100000;
    public static final int WILL_QOS = 0b00011000; // FIXME
    public static final int WILL_FLAG = 0b00000100;
    public static final int CLEAN_SESSION = 0b00000010;

    private ConnectFlags() {

    }

    public static String toString(int flags) {
        return "ConnectFlags[" +
                "USER_NAME = " + ((flags & USER_NAME) > 0) + ", " +
                "PASSWORD_FLAG = " + ((flags & PASSWORD_FLAG) > 0) + ", " +
                "WILL_RETAIN = " + ((flags & WILL_RETAIN) > 0) + ", " +
                "WILL_QOS = " + ((flags & WILL_QOS) > 0) + ", " +
                "WILL_FLAG = " + ((flags & WILL_FLAG) > 0) + ", " +
                "CLEAN_SESSION = " + ((flags & CLEAN_SESSION) > 0) +
                "]";
    }

}
