package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RegisterResponseMessage implements ControlMessage {
    public enum RegisterError {
        ALREADY_REGISTERED
    }

    public static final RegisterResponseMessage SUCCESS = new RegisterResponseMessage(null);
    public static final RegisterResponseMessage ERROR_ALREADY_REGISTERED = new RegisterResponseMessage(RegisterError.ALREADY_REGISTERED);
    private RegisterError error;

    private RegisterResponseMessage(RegisterError error) {
        this.error = error;
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.REGISTER_RESPONSE;
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

    public RegisterError getError() {
        return error;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }

    static RegisterResponseMessage readFromBuffer(ByteBuf buffer) {
        boolean success = buffer.readByte() == 1;
        if (!success) {
            int ordinal = buffer.readByte();
            return new RegisterResponseMessage(RegisterError.values()[ordinal]);
        }
        return RegisterResponseMessage.SUCCESS;
    }
}
