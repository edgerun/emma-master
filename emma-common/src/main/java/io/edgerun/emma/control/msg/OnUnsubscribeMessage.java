package io.edgerun.emma.control.msg;

import java.nio.charset.Charset;

import io.edgerun.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class OnUnsubscribeMessage implements ControlMessage {
    private final String brokerId;
    private final String topic;

    public OnUnsubscribeMessage(String brokerId, String topic) {
        this.brokerId = brokerId;
        this.topic = topic;
    }

    OnUnsubscribeMessage(ByteBuf byteBuf) {
        int brokerIdLength = byteBuf.readInt();
        this.brokerId = byteBuf.readCharSequence(brokerIdLength, Charset.forName("UTF-8")).toString();
        int topicLength = byteBuf.readInt();
        this.topic = byteBuf.readCharSequence(topicLength, Charset.forName("UTF-8")).toString();
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.ON_UNSUBSCRIBE;
    }

    public String getBrokerId() {
        return brokerId;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeInt(brokerId.length());
        buffer.writeCharSequence(brokerId, Charset.forName("UTF-8"));
        buffer.writeInt(topic.length());
        buffer.writeCharSequence(topic, Charset.forName("UTF-8"));
    }
}
