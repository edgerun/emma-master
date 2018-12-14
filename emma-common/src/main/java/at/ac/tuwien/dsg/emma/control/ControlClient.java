package at.ac.tuwien.dsg.emma.control;

import at.ac.tuwien.dsg.emma.control.msg.ControlMessage;
import at.ac.tuwien.dsg.emma.control.msg.ControlPacketCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ControlClient {
    private final String host;
    private final int port;

    public ControlClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public <Response extends ControlMessage> Response connectAndRequest(final ControlMessage request) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ClientHandler clientHandler = new ClientHandler(request);
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress(host, port));
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 1, 4))
                            .addLast(new ControlPacketCodec())
                            .addLast(clientHandler)
                            .addLast(new ControlPacketCodec());
                }
            });
            clientBootstrap.connect().sync();
            return (Response) clientHandler.getResponse();
        } catch (InterruptedException e) {
            return null;
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {
        private final ControlMessage request;
        private final BlockingQueue<Object> response = new LinkedBlockingQueue<>(1);

        ClientHandler(ControlMessage request) {
            this.request = request;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(request);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.close();
            response.add(msg);
        }

        Object getResponse() {
            try {
                return response.take();
            } catch (InterruptedException e) {
                return null;
            }
        }
    }
}
