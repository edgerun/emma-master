package at.ac.tuwien.dsg.emma.manager.network.monitoring;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringLoop;
import at.ac.tuwien.dsg.emma.monitoring.MonitoringMessageHandlerAdapter;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.util.Concurrent;
import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * MonitoringService.
 */
@Component
public class MonitoringService implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringService.class);

    private static AtomicInteger messageIds = new AtomicInteger();
    private static IntUnaryOperator messageIdUpdater = i -> i >= Integer.MAX_VALUE ? 0 : i + 1;

    @Autowired
    private Executor monitoringCommandExecutor;

    private Thread thread;

    private MonitoringLoop monitoringLoop;

    @Autowired
    public MonitoringService(
            @Value("${emma.manager.address}") String address,
            @Value("${emma.manager.monitoring.port}") Integer port) {
        this(new InetSocketAddress(address, port));
    }

    public MonitoringService(InetSocketAddress bind) {
        this.monitoringLoop = new MonitoringLoop(bind);
        this.monitoringLoop.setReadHandler(new ReadHandler());
    }

    public void pingRequest(String sourceHost, String targetHost) {
        for (int i = 0; i < 10; i++) {
            PingReqMessage message = new PingReqMessage(messageIds.updateAndGet(messageIdUpdater));

            message.setSource(monitoringLoop.getBindAddress());
            message.setDestination(new InetSocketAddress(sourceHost, 60043));
            message.setTargetHost(targetHost);
            message.setTargetPort(60043);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending ping request message {}", message);
            }

            monitoringLoop.send(message);
            Concurrent.sleep(10);
        }
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
        public void onMessage(MonitoringLoop loop, PingRespMessage message) {
            LOG.info("Received ping response message for request id {}: {}", message.getRequestId(), message.getLatency());
        }
    }

}
