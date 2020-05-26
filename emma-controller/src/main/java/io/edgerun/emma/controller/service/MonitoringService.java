package io.edgerun.emma.controller.service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import io.edgerun.emma.controller.event.ClientConnectEvent;
import io.edgerun.emma.controller.event.LatencyUpdateEvent;
import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.BrokerRepository;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.ClientRepository;
import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.monitoring.MonitoringLoop;
import io.edgerun.emma.monitoring.MonitoringMessageHandlerAdapter;
import io.edgerun.emma.monitoring.msg.PingReqMessage;
import io.edgerun.emma.monitoring.msg.PingRespMessage;
import io.edgerun.emma.monitoring.msg.ReconnectAck;
import io.edgerun.emma.monitoring.msg.ReconnectRequest;
import io.edgerun.emma.monitoring.msg.UsageRequest;
import io.edgerun.emma.monitoring.msg.UsageResponse;
import io.edgerun.emma.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.edgerun.emma.controller.network.Link;
import io.edgerun.emma.controller.network.Metrics;
import io.edgerun.emma.controller.network.NetworkManager;
import io.edgerun.emma.controller.network.graph.Edge;

/**
 * MonitoringService.
 */
@Component
public class MonitoringService implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private static AtomicInteger messageIds = new AtomicInteger();
    private static IntUnaryOperator messageIdUpdater = i -> i >= Integer.MAX_VALUE ? 0 : i + 1;

    private ApplicationEventPublisher systemEvents;
    private NetworkManager networkManager;
    private BrokerRepository brokerRepository;
    private ClientRepository clientRepository;

    private MonitoringLoop monitoringLoop;
    private Thread thread;

    private Map<Integer, PingRequest> pingRequests; // TODO: TTL

    @Autowired
    public MonitoringService(
            @Value("${emma.controller.address}") String address,
            @Value("${emma.controller.monitoring.port}") Integer port) {
        this(new InetSocketAddress(address, port));
    }

    public MonitoringService(InetSocketAddress bind) {
        this.monitoringLoop = new MonitoringLoop(bind);
        this.monitoringLoop.setReadHandler(new ReadHandler());
        this.pingRequests = new HashMap<>();
    }

    @Autowired
    public void setSystemEvents(ApplicationEventPublisher systemEvents) {
        this.systemEvents = systemEvents;
    }

    @Autowired
    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Autowired
    public void setBrokerRepository(BrokerRepository brokerRepository) {
        this.brokerRepository = brokerRepository;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Scheduled(fixedDelay = 15000)
    public void update() {
        // TODO: issue requests in a way that distributes sources
        for (Edge<Host, Link> edge : networkManager.getNetwork().getEdges()) {
            Host source;
            Host target;

            if (edge.getNodeU().getValue() instanceof Client) {
                source = edge.getNodeU().getValue();
                target = edge.getNodeV().getValue();
            } else {
                source = edge.getNodeV().getValue();
                target = edge.getNodeU().getValue();
            }

            pingRequest(source, target);
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void updateUsage() {
        for (Broker broker : brokerRepository.getHosts().values()) {
            if (broker.isAlive()) {
                usageRequest(broker);
            }
        }
    }

    public void usageRequest(Broker host) {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Sending usage request to {}", host.getId());
        }

        UsageRequest request = new UsageRequest(host.getId());
        request.setDestination(host.getMonitoringAddress());
        monitoringLoop.send(request);
    }

    public void pingRequest(Host source, Host target) {
        PingReqMessage message = pingRequest(source.getMonitoringAddress(), target.getMonitoringAddress());
        pingRequests.put(message.getRequestId(), new PingRequest(message, source, target));
    }

    public void reconnect(Client client, Broker broker) {
        ReconnectRequest reconnect = new ReconnectRequest(client.getId(), broker.getId());
        reconnect.setDestination(client.getMonitoringAddress());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending reconnect message {}", reconnect);
        }

        monitoringLoop.send(reconnect);
    }

    public PingReqMessage pingRequest(InetSocketAddress source, InetSocketAddress target) {
        PingReqMessage message = new PingReqMessage(messageIds.updateAndGet(messageIdUpdater));

        message.setSource(monitoringLoop.getBindAddress());
        message.setDestination(source);
        message.setTargetHost(target.getAddress());
        message.setTargetPort(target.getPort());

        if (LOG.isTraceEnabled()) {
            LOG.trace("Sending ping request message {}", message);
        }

        monitoringLoop.send(message);
        return message;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Starting monitoring service on {}", monitoringLoop.getBindAddress());
        thread = new Thread(monitoringLoop, "emma.Monitoring");
        thread.start();
    }

    @Override
    public void destroy() throws Exception {
        IOUtils.close(monitoringLoop);

        if (thread != null) {
            thread.interrupt();
        }
    }

    private class ReadHandler extends MonitoringMessageHandlerAdapter {
        @Override
        public void onMessage(MonitoringLoop loop, PingRespMessage resp) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Ping request {} response {}", resp.getRequestId(), resp.getLatency());
            }

            PingRequest req = pingRequests.remove(resp.getRequestId());
            if (req == null) {
                return;
            }

            systemEvents.publishEvent(new LatencyUpdateEvent(req.getSource(), req.getTarget(), resp.getLatency()));
        }

        @Override
        public void onMessage(MonitoringLoop loop, UsageResponse message) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Received usage response {}", message);
            }

            Metrics metrics = brokerRepository.getById(message.getHostId()).getMetrics();

            metrics.set("load", message.getLoad());
            metrics.set("processors", message.getProcessors());
        }

        @Override
        public void onMessage(MonitoringLoop loop, ReconnectAck message) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Received reconnect ack {}", message);
            }

            Client client = clientRepository.getById(message.getClientId());
            Broker broker = brokerRepository.getById(message.getBrokerHost());

            systemEvents.publishEvent(new ClientConnectEvent(client, broker));
        }
    }

    private static class PingRequest {
        private long created;
        private PingReqMessage request;
        private Host source;
        private Host target;

        public PingRequest(PingReqMessage request, Host source, Host target) {
            this.request = request;
            this.source = source;
            this.target = target;
            this.created = System.currentTimeMillis();
        }

        public long getCreated() {
            return created;
        }

        public PingReqMessage getRequest() {
            return request;
        }

        public Host getSource() {
            return source;
        }

        public Host getTarget() {
            return target;
        }
    }

}
