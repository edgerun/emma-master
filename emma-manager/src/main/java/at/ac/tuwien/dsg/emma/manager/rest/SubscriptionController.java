package at.ac.tuwien.dsg.emma.manager.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
