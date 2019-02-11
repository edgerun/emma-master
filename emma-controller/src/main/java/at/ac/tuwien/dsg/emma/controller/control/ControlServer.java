package at.ac.tuwien.dsg.emma.controller.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import at.ac.tuwien.dsg.emma.control.msg.ControlPacketCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ControlServer implements ApplicationContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(ControlServer.class);
    private ApplicationContext context;
    private NioEventLoopGroup managerGroup;
    private NioEventLoopGroup workerGroup;
    private int port;

    public ControlServer(int port) {
        this.port = port;
    }

    public void start() {
        managerGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(managerGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        channel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 1, 4))
                                .addLast(new ControlPacketCodec())
                                .addLast(context.getBean(ControlServerInboundAdapter.class))
                                .addLast(new ControlPacketCodec());
                    }
                });
        try {
            bootstrap.bind(port).sync();
            LOG.info("ControlServer listening on port {}", port);
        } catch (InterruptedException e) {
            LOG.error("Could not bind ControlServer", e);
        }
    }

    public void stop() {
        LOG.info("Shutting down manager group");
        managerGroup.shutdownGracefully();
        LOG.info("Shutting down worker group");
        workerGroup.shutdownGracefully();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
