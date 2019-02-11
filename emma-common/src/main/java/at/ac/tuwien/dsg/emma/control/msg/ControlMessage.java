package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface ControlMessage {
    ControlPacketType getPacketType();

    void writeToBuffer(ByteBuf buffer);

    void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx);
}
