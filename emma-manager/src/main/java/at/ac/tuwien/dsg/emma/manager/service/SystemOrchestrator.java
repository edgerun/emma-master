package at.ac.tuwien.dsg.emma.manager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.event.BrokerConnectEvent;
import at.ac.tuwien.dsg.emma.manager.event.BrokerDisconnectEvent;
import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;

@Component
@Async
public class SystemOrchestrator {

    private static final Logger LOG = LoggerFactory.getLogger(SystemOrchestrator.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private NetworkManager networkManager;

    @Autowired
    private MonitoringService monitoringService;

    @EventListener
    void onEvent(BrokerConnectEvent event) {
        LOG.info("Broker connected {}", event);

        networkManager.add(event.getBroker());

        for (Broker brokerInfo : brokerRepository.getHosts().values()) {
            // TODO: this is questionable
            if (brokerInfo == event.getBroker()) {
                continue;
            }

            monitoringService.pingRequest(event.getBroker().getHost(), brokerInfo.getHost());
        }
    }

    @EventListener
    void onEvent(BrokerDisconnectEvent event) {
        LOG.info("Broker disconnected {}", event);

        networkManager.remove(event.getBroker());
    }
}
