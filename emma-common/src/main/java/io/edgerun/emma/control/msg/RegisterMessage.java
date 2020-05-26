package io.edgerun.emma.control.msg;

import java.nio.charset.Charset;

import io.edgerun.emma.NodeInfo;
import io.edgerun.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class RegisterMessage implements ControlMessage {
    private final NodeType nodeType;
    private final String host;
    private final int port;
    private final int monitoringPort;

    public RegisterMessage(NodeType nodeType, String host, int port, int monitoringPort) {
        this.nodeType = nodeType;
        this.host = host;
        this.port = port;
        this.monitoringPort = monitoringPort;
    }

    public RegisterMessage(NodeType nodeType, NodeInfo nodeInfo) {
        this(nodeType, nodeInfo.getHost(), nodeInfo.getPort(), nodeInfo.getMonitoringPort());
    }

    RegisterMessage(ByteBuf byteBuf) {
        this.nodeType = NodeType.values()[byteBuf.readByte()];
        int hostLength = byteBuf.readInt();
        this.host = byteBuf.readCharSequence(hostLength, Charset.forName("UTF-8")).toString();
        this.port = byteBuf.readInt();
        this.monitoringPort = byteBuf.readInt();
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public ControlPacketType getPacketType() {
        return ControlPacketType.REGISTER;
    }

    @Override
    public void callHandler(ControlMessageHandler handler, ChannelHandlerContext ctx) {
        handler.handleMessage(this, ctx);
    }

    @Override
    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeByte(nodeType.ordinal());
        buffer.writeInt(host.length());
        buffer.writeCharSequence(host, Charset.forName("UTF-8"));
        buffer.writeInt(port);
        buffer.writeInt(monitoringPort);
    }

    public NodeInfo toNodeInfo() {
        return new NodeInfo(host, port, monitoringPort);
    }
}
