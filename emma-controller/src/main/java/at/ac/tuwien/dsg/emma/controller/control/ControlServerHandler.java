package at.ac.tuwien.dsg.emma.controller.control;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.control.msg.*;
import at.ac.tuwien.dsg.emma.controller.event.ClientConnectEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientDeregisterEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientRegisterEvent;
import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.ClientRepository;
import at.ac.tuwien.dsg.emma.controller.network.NetworkManager;
import at.ac.tuwien.dsg.emma.controller.network.sel.BrokerSelectionStrategy;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static at.ac.tuwien.dsg.emma.control.msg.NodeType.BROKER;
import static at.ac.tuwien.dsg.emma.control.msg.NodeType.CLIENT_GATEWAY;

@Component
public class ControlServerHandler implements ControlMessageHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ControlServerHandler.class);
    private final ApplicationEventPublisher systemEvents;
    private final BrokerRepository brokerRepository;
    private final ClientRepository clientRepository;
    private final BrokerSelectionStrategy brokerSelectionStrategy;
    private final NetworkManager networkManager;

    @Autowired
    public ControlServerHandler(ApplicationEventPublisher systemEvents, BrokerRepository brokerRepository,
                                ClientRepository clientRepository, BrokerSelectionStrategy brokerSelectionStrategy,
                                NetworkManager networkManager) {
        this.systemEvents = systemEvents;
        this.brokerRepository = brokerRepository;
        this.clientRepository = clientRepository;
        this.brokerSelectionStrategy = brokerSelectionStrategy;
        this.networkManager = networkManager;
    }

    @Override
    public void handleMessage(RegisterMessage registerMessage, ChannelHandlerContext ctx) {
        NodeInfo info = registerMessage.toNodeInfo();
        if (info.isHostWildcard()) {
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            String remoteHost;
            if (remoteAddress instanceof InetSocketAddress) {
                remoteHost = ((InetSocketAddress) remoteAddress).getHostString();
            } else {
                remoteHost = remoteAddress.toString();
            }
            LOG.debug("Host is a wildcard, using remote address of request {}", remoteHost);
            info.setHost(remoteHost);
        }

        switch (registerMessage.getNodeType()) {
            case CLIENT_GATEWAY:
                registerClient(ctx, info);
                break;
            case BROKER:
                registerBroker(ctx, info);
                break;
        }
    }

    private void registerClient(ChannelHandlerContext ctx, NodeInfo info) {
        if (clientRepository.getHost(info.getHost(), info.getPort()) != null) {
            LOG.debug("Host already registered");
            ctx.writeAndFlush(RegisterResponseMessage.ERROR_ALREADY_REGISTERED);
            return;
        }

        LOG.info("Registering new client {}", info);

        Client registered = clientRepository.register(info);
        ctx.writeAndFlush(new RegisterResponseMessage(registered.getId()));
        systemEvents.publishEvent(new ClientRegisterEvent(registered));
    }

    private void registerBroker(ChannelHandlerContext ctx, NodeInfo info) {
        if (brokerRepository.getHost(info.getHost(), info.getPort()) != null) {
            LOG.debug("Broker already registered");
            ctx.writeAndFlush(RegisterResponseMessage.ERROR_ALREADY_REGISTERED);
            return;
        }

        LOG.info("Registering new broker {}", info);

        Broker registered = brokerRepository.register(info);
        ctx.writeAndFlush(new RegisterResponseMessage(registered.getId()));
    }

    @Override
    public void handleMessage(UnregisterMessage unregisterMessage, ChannelHandlerContext ctx) {
        String id = unregisterMessage.getId();

        switch (unregisterMessage.getNodeType()) {
            case CLIENT_GATEWAY:
                unregisterClient(ctx, id);
                break;
            case BROKER:
                unregisterBroker(ctx, id);
                break;
        }
    }

    private void unregisterClient(ChannelHandlerContext ctx, String id) {
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

    private void unregisterBroker(ChannelHandlerContext ctx, String id) {
        Broker broker = brokerRepository.getById(id);

        if (broker == null) {
            ctx.writeAndFlush(UnregisterResponseMessage.ERROR_NO_REGISTRATION);
            return;
        }

        boolean removed = brokerRepository.remove(broker);
        if (removed) {
            ctx.writeAndFlush(UnregisterResponseMessage.SUCCESS);
        }
    }

    @Override
    public void handleMessage(GetBrokerMessage getBrokerMessage, ChannelHandlerContext ctx) {
        String gatewayId = getBrokerMessage.getGatewayId();
        LOG.debug("Client {} requests broker URI", gatewayId);

        Client client = clientRepository.getById(gatewayId);
        if (client == null) {
            ctx.writeAndFlush(GetBrokerResponseMessage.ERROR_UNKNOWN_GATEWAY_ID);
            return;
        }

        // TODO: bootstrap to root broker instead
        try {
            Broker broker = brokerSelectionStrategy.select(client, networkManager.getNetwork());

            if (broker == null) {
                throw new IllegalStateException("No broker in networks");
            }

            LOG.info("Selected broker for client {}: {}", client, broker);
            systemEvents.publishEvent(new ClientConnectEvent(client, broker));
            String brokerUri = "tcp://" + broker.getHost() + ":" + broker.getPort();
            ctx.writeAndFlush(new GetBrokerResponseMessage(brokerUri));
        } catch (IllegalStateException e) {
            LOG.info("No broker connected {}", e.getMessage());
            ctx.writeAndFlush(GetBrokerResponseMessage.ERROR_NO_BROKER_AVAILABLE);
        }
    }
}
