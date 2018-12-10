package at.ac.tuwien.dsg.emma.control.msg;

import io.netty.channel.ChannelHandlerContext;

public interface ControlMessageHandler {
    default void handleMessage(RegisterMessage registerMessage, ChannelHandlerContext ctx) {
    }

    default void handleMessage(UnregisterMessage unregisterMessage, ChannelHandlerContext ctx) {
    }

    default void handleMessage(RegisterResponseMessage registerResponseMessage, ChannelHandlerContext ctx) {
    }

    default void handleMessage(UnregisterResponseMessage unregisterResponseMessage, ChannelHandlerContext ctx) {
    }
}
