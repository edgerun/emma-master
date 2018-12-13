package at.ac.tuwien.dsg.emma.control.msg;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.control.ControlPacketType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;

public class RegisterMessage implements ControlMessage {
    private String host;
    private int port;
    private int monitoringPort;

    public RegisterMessage(String host, int port, int monitoringPort) {
        this.host = host;
        this.port = port;
        this.monitoringPort = monitoringPort;
    }

    public RegisterMessage(NodeInfo nodeInfo) {
        this(nodeInfo.getHost(), nodeInfo.getPort(), nodeInfo.getMonitoringPort());
    }

    RegisterMessage(ByteBuf byteBuf) {
        int hostLength = byteBuf.readInt();
        this.host = byteBuf.readCharSequence(hostLength, Charset.forName("UTF-8")).toString();
        this.port = byteBuf.readInt();
        this.monitoringPort = byteBuf.readInt();
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
        buffer.writeInt(host.length());
        buffer.writeCharSequence(host, Charset.forName("UTF-8"));
        buffer.writeInt(port);
        buffer.writeInt(monitoringPort);
    }

    public NodeInfo toNodeInfo() {
        return new NodeInfo(host, port, monitoringPort);
    }
}
