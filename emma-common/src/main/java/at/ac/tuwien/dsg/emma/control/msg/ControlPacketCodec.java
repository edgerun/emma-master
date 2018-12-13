package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.control.CodecException;
import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class ControlPacketCodec extends ByteToMessageCodec<ControlMessage> {
    @Override
    protected void encode(ChannelHandlerContext context, ControlMessage controlMessage, ByteBuf byteBuf) throws Exception {
        // write packet type
        byteBuf.writeByte(controlMessage.getPacketType().getId());

        // write packet data to a temporary buffer
        ByteBuf buffer = context.alloc().buffer();
        controlMessage.writeToBuffer(buffer);

        // write length and data
        byteBuf.writeInt(buffer.readableBytes() + 5);
        byteBuf.writeBytes(buffer);
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list) throws Exception {
        ControlPacketType packetType = ControlPacketType.fromId(byteBuf.readByte());
        byteBuf.readInt(); // skip length field
        switch (packetType) {
            case RESERVED_0:
                throw new CodecException("Cannot decode packet of type 0");
            case REGISTER:
                list.add(new RegisterMessage(byteBuf));
                break;
            case REGISTER_RESPONSE:
                list.add(new RegisterResponseMessage(byteBuf));
                break;
            case UNREGISTER:
                list.add(new UnregisterMessage(byteBuf));
                break;
            case UNREGISTER_RESPONSE:
                list.add(new UnregisterResponseMessage(byteBuf));
                break;
            case GET_BROKER:
                list.add(new GetBrokerMessage(byteBuf));
                break;
            case GET_BROKER_RESPONSE:
                list.add(new GetBrokerResponseMessage(byteBuf));
                break;
            default:
                throw new CodecException("Invalid packet type");
        }
    }
}
