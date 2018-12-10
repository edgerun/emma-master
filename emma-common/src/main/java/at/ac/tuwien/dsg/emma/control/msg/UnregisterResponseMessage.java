package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UnregisterResponseMessage implements ControlMessage {
    public enum UnregisterError {
        NO_REGISTRATION
    }

    public static final UnregisterResponseMessage SUCCESS = new UnregisterResponseMessage(null);
    public static final UnregisterResponseMessage ERROR_NO_REGISTRATION = new UnregisterResponseMessage(UnregisterError.NO_REGISTRATION);
    private UnregisterError error;

    private UnregisterResponseMessage(UnregisterError error) {
        this.error = error;
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.UNREGISTER_RESPONSE;
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeByte(isSuccess() ? 1 : 0);
        if (error != null) {
            buffer.writeByte(error.ordinal());
        }
    }

    public boolean isSuccess() {
        return error == null;
    }

    public UnregisterError getError() {
        return error;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }

    static UnregisterResponseMessage readFromBuffer(ByteBuf buffer) {
        boolean success = buffer.readByte() == 1;
        if (!success) {
            int ordinal = buffer.readByte();
            return new UnregisterResponseMessage(UnregisterError.values()[ordinal]);
        }
        return UnregisterResponseMessage.SUCCESS;
    }
}
