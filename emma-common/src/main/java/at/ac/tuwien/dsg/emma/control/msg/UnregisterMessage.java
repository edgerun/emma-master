package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class UnregisterMessage implements ControlMessage {
    private String id;

    public UnregisterMessage(String id) {
        this.id = id;
    }

    UnregisterMessage(ByteBuf byteBuf) {
        int idLength = byteBuf.readInt();
        this.id = byteBuf.readCharSequence(idLength, Charset.forName("UTF-8")).toString();
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.UNREGISTER;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(id.length());
        buffer.writeCharSequence(id, Charset.forName("UTF-8"));
    }

    public String getId() {
        return id;
    }
}
