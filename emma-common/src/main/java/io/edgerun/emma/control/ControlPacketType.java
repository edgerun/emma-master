package io.edgerun.emma.control;

public enum ControlPacketType {
    RESERVED_0,
    REGISTER,
    REGISTER_RESPONSE,
    UNREGISTER,
    UNREGISTER_RESPONSE,
    GET_BROKER,
    GET_BROKER_RESPONSE,
    ON_SUBSCRIBE,
    ON_UNSUBSCRIBE;

    public int getId() {
        return ordinal();
    }

    public static ControlPacketType fromId(int id) {
        return values()[id];
    }
}
