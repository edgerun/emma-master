package io.edgerun.emma.control.msg;

import java.nio.charset.Charset;

import io.edgerun.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class GetBrokerMessage implements ControlMessage {
    private String gatewayId;

    GetBrokerMessage(ByteBuf byteBuf) {
        int gatewayIdLength = byteBuf.readInt();
        this.gatewayId = byteBuf.readCharSequence(gatewayIdLength, Charset.forName("UTF-8")).toString();
    }

    public GetBrokerMessage(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.GET_BROKER;
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(gatewayId.length());
        buffer.writeCharSequence(gatewayId, Charset.forName("UTF-8"));
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }
}
