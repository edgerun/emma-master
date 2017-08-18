package at.ac.tuwien.dsg.emma.manager.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.bridge.BridgingTable;
import at.ac.tuwien.dsg.emma.bridge.BridgingTableEntry;
import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.service.sub.Subscription;
import at.ac.tuwien.dsg.emma.manager.service.sub.SubscriptionTable;

/**
 * SubscriptionController.
 */
@RestController
public class SubscriptionController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private SubscriptionTable subscriptionTable;

    @Autowired
    private BridgingTable bridgingTable;

    @RequestMapping(value = "/broker/onSubscribe", method = RequestMethod.GET)
    public void onSubscribe(String id, String topic) {
        LOG.debug("/broker/onSubscribe({},{})", id, topic);

        Broker broker = brokerRepository.getById(id);

        if (broker == null) {
            LOG.warn("Broker with host {} not found", id);
            // FIXME
            return;
        }

        Subscription subscription = subscriptionTable.getOrCreate(broker, topic);
//        if (subscription.getCount() == 0) {
            updateRoutes(broker, topic);
//        }
        subscription.increment();

        LOG.debug("Updated subscription {}", subscription);
    }

    @RequestMapping(value = "/broker/onUnsubscribe", method = RequestMethod.GET)
    public void onUnsubscribe(String id, String topic) {
        LOG.debug("/broker/onUnsubscribe({},{})", id, topic);

        Broker broker = brokerRepository.getById(id);
        if (broker == null) {
            LOG.warn("Broker with host {} not found", id);
            // FIXME
            return;
        }

        Subscription subscription = subscriptionTable.get(broker, topic);

        if (subscription != null) {
            subscription.decrement();
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
