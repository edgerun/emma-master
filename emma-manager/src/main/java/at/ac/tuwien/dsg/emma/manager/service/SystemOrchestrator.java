package at.ac.tuwien.dsg.emma.manager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        networkManager.add(event.getHost());

        for (Broker brokerInfo : brokerRepository.getHosts().values()) {
            // TODO: this is questionable
            if (brokerInfo == event.getHost()) {
                continue;
            }

            monitoringService.pingRequest(event.getHost(), brokerInfo);
        }
    }

    @EventListener
    void onEvent(BrokerDisconnectEvent event) {
        LOG.info("Broker disconnected {}", event);

        networkManager.remove(event.getHost());
        subscriptionTable.remove(event.getHost());
        removeBridgeEntries(event.getHost());
    }

    @EventListener
    void onEvent(SubscribeEvent event) {
        LOG.debug("Broker subscribed {}", event);

        Subscription subscription = subscriptionTable.getOrCreate(event.getHost(), event.getTopic());
        addBridgeEntries(event.getHost(), event.getTopic());
    }

    @EventListener
    void onEvent(UnsubscribeEvent event) {
        LOG.debug("Broker unsubscribed {}", event);

        Subscription subscription = subscriptionTable.get(event.getHost(), event.getTopic());

        if (subscription != null) {
            subscriptionTable.remove(subscription);
            removeBridgeEntries(event.getHost(), event.getTopic());
        }
    }

    private void removeBridgeEntries(Broker bridge) {
        bridgingTable.deleteBridge(bridge.getId());
        debugBridgingTable();
    }

    private void removeBridgeEntries(Broker destination, String topic) {
        Collection<BridgingTableEntry> entries = bridgingTable.getAll().stream()
                .filter(e -> Objects.equals(e.getSource(), destination.getId()))
                .filter(e -> Objects.equals(e.getTopic(), topic))
                .collect(Collectors.toList());

        bridgingTable.delete(entries);
        debugBridgingTable();
    }

    private void addBridgeEntries(Broker destination, String topic) {
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
        debugBridgingTable();
    }

    private void debugBridgingTable() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated bridging table:");
            for (BridgingTableEntry entry : bridgingTable.getAll()) {
                LOG.debug("  {}", entry);
            }
        }
    }
}
