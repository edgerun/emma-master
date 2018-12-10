package at.ac.tuwien.dsg.emma.control;

public enum ControlPacketType {
    RESERVED_0,
    REGISTER,
    UNREGISTER,
    REGISTER_RESPONSE,
    UNREGISTER_RESPONSE;

    public int getId() {
        return ordinal();
    }

    public static ControlPacketType fromId(int id) {
        return values()[id];
    }
}
