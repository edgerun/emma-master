package io.edgerun.emma.controller.control;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.edgerun.emma.NodeInfo;
import io.edgerun.emma.control.msg.ControlMessageHandler;
import io.edgerun.emma.control.msg.GetBrokerMessage;
import io.edgerun.emma.control.msg.GetBrokerResponseMessage;
import io.edgerun.emma.control.msg.OnSubscribeMessage;
import io.edgerun.emma.control.msg.OnUnsubscribeMessage;
import io.edgerun.emma.control.msg.RegisterMessage;
import io.edgerun.emma.control.msg.RegisterResponseMessage;
import io.edgerun.emma.control.msg.UnregisterMessage;
import io.edgerun.emma.control.msg.UnregisterResponseMessage;
import io.edgerun.emma.controller.event.ClientConnectEvent;
import io.edgerun.emma.controller.event.ClientDeregisterEvent;
import io.edgerun.emma.controller.event.ClientRegisterEvent;
import io.edgerun.emma.controller.event.SubscribeEvent;
import io.edgerun.emma.controller.event.UnsubscribeEvent;
import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.BrokerRepository;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.ClientRepository;
import io.edgerun.emma.controller.network.NetworkManager;
import io.edgerun.emma.controller.network.sel.BrokerSelectionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;

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

    @Override
    public void handleMessage(OnSubscribeMessage message, ChannelHandlerContext ctx) {
        String id = message.getBrokerId();
        String topic = message.getTopic();
        LOG.debug("Broker {} subscribes topic {}", id, topic);

        Broker broker = brokerRepository.getById(id);

        if (broker == null) {
            LOG.warn("Broker with id {} not found", id);
            // FIXME
            return;
        }

        systemEvents.publishEvent(new SubscribeEvent(broker, topic));
    }

    @Override
    public void handleMessage(OnUnsubscribeMessage message, ChannelHandlerContext ctx) {
        String id = message.getBrokerId();
        String topic = message.getTopic();
        LOG.debug("Broker {} unsubscribes topic {}", id, topic);

        Broker broker = brokerRepository.getById(id);
        if (broker == null) {
            LOG.warn("Broker with id {} not found", id);
            // FIXME
            return;
        }

        systemEvents.publishEvent(new UnsubscribeEvent(broker, topic));
    }
}
