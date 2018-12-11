package at.ac.tuwien.dsg.emma.controller.control;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.control.msg.*;
import at.ac.tuwien.dsg.emma.controller.event.ClientDeregisterEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientRegisterEvent;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.ClientRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ControlServerHandler implements ControlMessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControlServerHandler.class);
    private ApplicationEventPublisher systemEvents;
    private ClientRepository clientRepository;

    @Autowired
    public ControlServerHandler(ApplicationEventPublisher systemEvents, ClientRepository clientRepository) {
        this.systemEvents = systemEvents;
        this.clientRepository = clientRepository;
    }

    @Override
    public void handleMessage(RegisterMessage registerMessage, ChannelHandlerContext ctx) {
        NodeInfo info = registerMessage.toNodeInfo();
        if (info.isHostWildcard()) {
            String remoteAddress = ctx.channel().remoteAddress().toString();
            LOG.debug("Host is a wildcard, using remote address of request {}", remoteAddress);
            info.setHost(remoteAddress);
        }

        if (clientRepository.getHost(info.getHost(), info.getPort()) != null) {
            LOG.debug("Host already registered");
            ctx.writeAndFlush(RegisterResponseMessage.ERROR_ALREADY_REGISTERED);
            return;
        }

        LOG.info("Registering new client {}", info);

        Client registered = clientRepository.register(info);
        ctx.writeAndFlush(RegisterResponseMessage.SUCCESS);
        systemEvents.publishEvent(new ClientRegisterEvent(registered));
    }

    @Override
    public void handleMessage(UnregisterMessage unregisterMessage, ChannelHandlerContext ctx) {
        String id = unregisterMessage.getId();
        Client host = clientRepository.getById(id);

        if (host == null) {
            ctx.writeAndFlush(UnregisterResponseMessage.ERROR_NO_REGISTRATION);
            return;
        }

        boolean removed = clientRepository.remove(host);
        if (removed) {
            ctx.writeAndFlush(UnregisterResponseMessage.SUCCESS);
            systemEvents.publishEvent(new ClientDeregisterEvent(host));
        }
    }
}
