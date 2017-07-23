package at.ac.tuwien.dsg.emma.mqtt;

/**
 * ConnectFlags.
 */
public class ConnectFlags {

    public static final int USER_NAME = 0b10000000;
    public static final int PASSWORD_FLAG = 0b01000000;
    public static final int WILL_RETAIN = 0b00100000;
    public static final int WILL_FLAG = 0b00000100;
    public static final int CLEAN_SESSION = 0b00000010;

    private ConnectFlags() {

    }

    public static boolean hasUserName(int flags) {
        return hasFlag(flags, USER_NAME);
    }

    public static boolean hasPassword(int flags) {
        return hasFlag(flags, PASSWORD_FLAG);
    }

    public static boolean hasWill(int flags) {
        return hasFlag(flags, WILL_FLAG);
    }

    public static boolean hasWillRetain(int flags) {
        return hasFlag(flags, WILL_RETAIN);
    }

    public static int getWillQos(int flags) {
        return (flags >> 3) & 0x3;
    }

    public static boolean hasCleanSession(int flags) {
        return hasFlag(flags, CLEAN_SESSION);
    }

    public static boolean hasFlag(int flags, int flag) {
        return (flags & flag) > 0;
    }

    public static String toString(int flags) {
        return "ConnectFlags[" +
                "USER_NAME = " + ((flags & USER_NAME) > 0) + ", " +
                "PASSWORD_FLAG = " + ((flags & PASSWORD_FLAG) > 0) + ", " +
                "WILL_RETAIN = " + ((flags & WILL_RETAIN) > 0) + ", " +
                "WILL_QOS = " + ((flags >> 3) & 0x3) + ", " +
                "WILL_FLAG = " + ((flags & WILL_FLAG) > 0) + ", " +
                "CLEAN_SESSION = " + ((flags & CLEAN_SESSION) > 0) +
                "]";
    }
}
