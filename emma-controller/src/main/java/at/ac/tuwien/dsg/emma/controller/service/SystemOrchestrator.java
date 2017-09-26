package at.ac.tuwien.dsg.emma.controller.service;

import java.time.Instant;
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
import at.ac.tuwien.dsg.emma.controller.event.BrokerConnectEvent;
import at.ac.tuwien.dsg.emma.controller.event.BrokerDisconnectEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientConnectEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientDeregisterEvent;
import at.ac.tuwien.dsg.emma.controller.event.ClientRegisterEvent;
import at.ac.tuwien.dsg.emma.controller.event.LatencyUpdateEvent;
import at.ac.tuwien.dsg.emma.controller.event.SubscribeEvent;
import at.ac.tuwien.dsg.emma.controller.event.UnsubscribeEvent;
import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.network.Link;
import at.ac.tuwien.dsg.emma.controller.network.NetworkManager;
import at.ac.tuwien.dsg.emma.controller.service.sub.Subscription;
import at.ac.tuwien.dsg.emma.controller.service.sub.SubscriptionTable;

@Component
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

        Broker broker = event.getHost();
        networkManager.add(broker);

        for (Broker brokerInfo : brokerRepository.getHosts().values()) {
            // TODO: this is questionable
            if (brokerInfo == broker) {
                continue;
            }

            monitoringService.pingRequest(broker, brokerInfo);
        }
        addBridgingEntries(broker);


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

    @EventListener
    @Async
    void onEvent(LatencyUpdateEvent event) {
        LOG.debug("Latency update received, updating link information {}", event);

        Link link = networkManager.getLink(event.getSource(), event.getTarget());

        if (link == null) {
            LOG.warn("No link found between {} and {}", event.getSource(), event.getTarget());
            return;
        }

        link.getLatency().add(event.getLatency());
        LOG.debug("Latency is now {}", link.getLatency());
    }

    @EventListener
    @Async
    void onEvent(ClientRegisterEvent event) {
        LOG.info("Client registered {}", event.getHost());
        networkManager.add(event.getHost());
    }

    @EventListener
    @Async
    void onEvent(ClientDeregisterEvent event) {
        LOG.info("Client dergistered {}", event.getHost());
        networkManager.remove(event.getHost());
    }

    @EventListener
    @Async
    void onEvent(ClientConnectEvent event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating client connection {} -> {}", event.getClient(), event.getBroker());
        }

        Client client = event.getClient();
        client.setLastReconnect(Instant.now());

        // find old
        Broker old = client.getConnectedTo();
        if (old != null) {
            // TODO: remove all subscriptions
            Link link = networkManager.getLink(client, old);
            if (link != null) { // may be null if previous disconnected
                link.setConnected(false);
            }
        }
        Broker current = event.getBroker();

        networkManager.getLink(client, current).setConnected(true);
        client.setConnectedTo(current);
    }

    private void removeBridgeEntries(Broker bridge) {
        bridgingTable.deleteBridge(bridge.getId());
        debugBridgingTable();
    }

    private void removeBridgeEntries(Broker destination, String topic) {
        Collection<BridgingTableEntry> entries = bridgingTable.getAll().stream()
                .filter(e -> Objects.equals(e.getDestination(), destination.getId()))
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

    private void addBridgingEntries(Broker source) {
        // connect the newly connected broker as a source for all existing subscriptions
        List<BridgingTableEntry> collect = subscriptionTable.getSubscriptions()
                .stream()
                .map(sub -> new BridgingTableEntry(sub.getFilter(), source.getId(), sub.getBroker().getId()))
                .collect(Collectors.toList());
        bridgingTable.insert(collect);
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
