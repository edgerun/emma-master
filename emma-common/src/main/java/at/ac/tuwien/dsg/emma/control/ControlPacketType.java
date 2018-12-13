package at.ac.tuwien.dsg.emma.control;

public enum ControlPacketType {
    RESERVED_0,
    REGISTER,
    REGISTER_RESPONSE,
    UNREGISTER,
    UNREGISTER_RESPONSE,
    GET_BROKER,
    GET_BROKER_RESPONSE;

    public int getId() {
        return ordinal();
    }

    public static ControlPacketType fromId(int id) {
        return values()[id];
    }
}
