package at.ac.tuwien.dsg.emma.manager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.event.BrokerConnectEvent;
import at.ac.tuwien.dsg.emma.manager.event.BrokerDisconnectEvent;

@Component
@Async
public class SystemOrchestrator {

    private static final Logger LOG = LoggerFactory.getLogger(SystemOrchestrator.class);

    @EventListener
    void onEvent(BrokerConnectEvent event) {
        LOG.info("Broker connected {}", event);
    }

    @EventListener
    void onEvent(BrokerDisconnectEvent event) {
        LOG.info("Broker disconnected {}", event);
    }
}
