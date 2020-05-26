package io.edgerun.emma.control.msg;

import io.edgerun.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public interface ControlMessage {
    ControlPacketType getPacketType();

    void writeToBuffer(ByteBuf buffer);

    void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx);
}
