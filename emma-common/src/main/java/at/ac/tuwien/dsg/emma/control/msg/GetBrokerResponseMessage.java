package at.ac.tuwien.dsg.emma.control.msg;

import java.nio.charset.Charset;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class GetBrokerResponseMessage implements ControlMessage {
    public enum GetBrokerError {
        UNKNOWN_GATEWAY_ID, NO_BROKER_AVAILABLE
    }

    private GetBrokerError error;
    private String brokerUri;
    public static GetBrokerResponseMessage ERROR_UNKNOWN_GATEWAY_ID = new GetBrokerResponseMessage(GetBrokerError.UNKNOWN_GATEWAY_ID);
    public static GetBrokerResponseMessage ERROR_NO_BROKER_AVAILABLE = new GetBrokerResponseMessage(GetBrokerError.NO_BROKER_AVAILABLE);

    GetBrokerResponseMessage(ByteBuf byteBuf) {
        boolean isSuccess = byteBuf.readByte() == 1;
        if (isSuccess) {
            int brokerUriLength = byteBuf.readInt();
            this.brokerUri = byteBuf.readCharSequence(brokerUriLength, Charset.forName("UTF-8")).toString();
        } else {
            this.error = GetBrokerError.values()[byteBuf.readByte()];
        }
    }

    public GetBrokerResponseMessage(String brokerUri) {
        this.brokerUri = brokerUri;
    }

    private GetBrokerResponseMessage(GetBrokerError error) {
        this.error = error;
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.GET_BROKER_RESPONSE;
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeByte(isSuccess() ? 1 : 0);
        if (isSuccess()) {
            buffer.writeInt(brokerUri.length());
            buffer.writeCharSequence(brokerUri, Charset.forName("UTF-8"));
        } else {
            buffer.writeByte(error.ordinal());
        }
    }

    public boolean isSuccess() {
        return error == null;
    }

    public GetBrokerError getError() {
        return error;
    }

    public String getBrokerUri() {
        return brokerUri;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }
}
