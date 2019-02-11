package at.ac.tuwien.dsg.emma.controller.control;

import at.ac.tuwien.dsg.emma.control.msg.ControlMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ControlServerInboundAdapter extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ControlServerInboundAdapter.class);
    private ControlServerHandler handler;

    @Autowired
    public ControlServerInboundAdapter(ControlServerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ControlMessage controlMessage = (ControlMessage) msg;
        controlMessage.callHandler(handler, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("Exception caught in handler, closing channel", cause);
        ctx.close();
    }
}
