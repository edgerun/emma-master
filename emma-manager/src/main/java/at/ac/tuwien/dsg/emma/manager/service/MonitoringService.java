package at.ac.tuwien.dsg.emma.manager.service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.event.LatencyUpdateEvent;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.monitoring.MonitoringLoop;
import at.ac.tuwien.dsg.emma.monitoring.MonitoringMessageHandlerAdapter;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.util.IOUtils;

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

    private MonitoringLoop monitoringLoop;
    private Thread thread;

    private Map<Integer, PingRequest> pingRequests; // TODO: TTL

    @Autowired
    public MonitoringService(
            @Value("${emma.manager.address}") String address,
            @Value("${emma.manager.monitoring.port}") Integer port) {
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

    @Scheduled(fixedDelay = 30000)
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

    public void pingRequest(Host source, Host target) {
        PingReqMessage message = pingRequest(
                new InetSocketAddress(source.getHost(), source.getMonitoringPort()),
                new InetSocketAddress(target.getHost(), target.getMonitoringPort())
        );
        pingRequests.put(message.getRequestId(), new PingRequest(message, source, target));
    }

    public PingReqMessage pingRequest(InetSocketAddress source, InetSocketAddress target) {
        PingReqMessage message = new PingReqMessage(messageIds.updateAndGet(messageIdUpdater));

        message.setSource(monitoringLoop.getBindAddress());
        message.setDestination(source);
        message.setTargetHost(target.getHostString());
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
