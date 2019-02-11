package at.ac.tuwien.dsg.emma.control.msg;

import java.nio.charset.Charset;

import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class UnregisterMessage implements ControlMessage {
    private final NodeType nodeType;
    private final String id;

    public UnregisterMessage(NodeType nodeType, String id) {
        this.nodeType = nodeType;
        this.id = id;
    }

    UnregisterMessage(ByteBuf byteBuf) {
        this.nodeType = NodeType.values()[byteBuf.readByte()];
        int idLength = byteBuf.readInt();
        this.id = byteBuf.readCharSequence(idLength, Charset.forName("UTF-8")).toString();
    }

    public NodeType getNodeType() {
        return nodeType;
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
        buffer.writeByte(nodeType.ordinal());
        buffer.writeInt(id.length());
        buffer.writeCharSequence(id, Charset.forName("UTF-8"));
    }

    public String getId() {
        return id;
    }
}
