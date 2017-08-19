package at.ac.tuwien.dsg.emma.manager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.bridge.BridgingTable;
import at.ac.tuwien.dsg.emma.bridge.BridgingTableEntry;
import at.ac.tuwien.dsg.emma.manager.event.BrokerConnectEvent;
import at.ac.tuwien.dsg.emma.manager.event.BrokerDisconnectEvent;
import at.ac.tuwien.dsg.emma.manager.event.SubscribeEvent;
import at.ac.tuwien.dsg.emma.manager.event.UnsubscribeEvent;
import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.service.sub.Subscription;
import at.ac.tuwien.dsg.emma.manager.service.sub.SubscriptionTable;

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

    @Autowired
    private SubscriptionTable subscriptionTable;

    @Autowired
    private BridgingTable bridgingTable;

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

    @EventListener
    void onEvent(SubscribeEvent event) {
        Subscription subscription = subscriptionTable.getOrCreate(event.getBroker(), event.getTopic());
        updateRoutes(event.getBroker(), event.getTopic());
        LOG.debug("Updated subscription {}", subscription);
    }

    @EventListener
    void onEvent(UnsubscribeEvent event) {
        Subscription subscription = subscriptionTable.get(event.getBroker(), event.getTopic());

        if (subscription != null) {
            subscriptionTable.remove(subscription);
            // TODO: update routes
        }

        LOG.debug("Updated subscription {}", subscription);
    }

    private void updateRoutes(Broker destination, String topic) {
        // connects all existing brokers to the one where a subscription occurred
        Collection<Broker> brokers = brokerRepository.getHosts().values();
        List<BridgingTableEntry> entries = new ArrayList<>(brokers.size());

        for (Broker source : brokers) {
            if (Objects.equals(destination, source)) {
                continue;
            }

            entries.add(new BridgingTableEntry(topic, source.getId(), destination.getId()));
        }

        bridgingTable.insert(entries);

        LOG.info("Updated bridging table:");
        for (BridgingTableEntry entry : bridgingTable.getAll()) {
            LOG.info("  {}", entry);
        }
    }
}
