package at.ac.tuwien.dsg.emma.control.msg;

import java.nio.charset.Charset;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RegisterResponseMessage implements ControlMessage {
    public enum RegisterError {
        ALREADY_REGISTERED
    }

    public static final RegisterResponseMessage ERROR_ALREADY_REGISTERED = new RegisterResponseMessage(RegisterError.ALREADY_REGISTERED);
    private RegisterError error;
    private String id;

    private RegisterResponseMessage(RegisterError error) {
        this.error = error;
    }

    public RegisterResponseMessage(String id) {
        this.id = id;
    }

    RegisterResponseMessage(ByteBuf byteBuf) {
        boolean success = byteBuf.readByte() == 1;
        if (success) {
            int idLength = byteBuf.readInt();
            this.id = byteBuf.readCharSequence(idLength, Charset.forName("UTF-8")).toString();
        } else {
            int ordinal = byteBuf.readByte();
            this.error = RegisterError.values()[ordinal];
        }
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.REGISTER_RESPONSE;
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeByte(isSuccess() ? 1 : 0);
        if (error == null) {
            buffer.writeInt(id.length());
            buffer.writeCharSequence(id, Charset.forName("UTF-8"));
        } else {
            buffer.writeByte(error.ordinal());
        }
    }

    public boolean isSuccess() {
        return error == null;
    }

    public RegisterError getError() {
        return error;
    }

    public String getId() {
        return id;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }
}
