package at.ac.tuwien.dsg.emma.manager.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.network.Broker;
import at.ac.tuwien.dsg.emma.manager.network.BrokerRepository;
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
    public void onSubscribe(String topic, HttpServletRequest request) {
        Broker broker = brokerRepository.getBrokerByHost(request.getRemoteAddr());

        if (broker == null) {
            LOG.warn("Broker with host {} not found", request.getRemoteAddr());
            // FIXME
            return;
        }

        Subscription subscription = subscriptionTable.getOrCreate(broker, topic);
        subscription.increment();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated subscription {}", subscription);
        }
    }

    @RequestMapping(value = "/broker/onUnsubscribe", method = RequestMethod.GET)
    public void onUnsubscribe(String topic, HttpServletRequest request) {
        LOG.info("/broker/onUnsubscribe(request: {})", topic, request.getRemoteAddr());

        Broker broker = brokerRepository.getBrokerByHost(request.getRemoteAddr());
        if (broker == null) {
            LOG.warn("Broker with host {} not found", request.getRemoteHost());
            // FIXME
            return;
        }

        Subscription subscription = subscriptionTable.get(broker, topic);

        if (subscription != null) {
            subscription.decrement();
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated subscription {}", subscription);
        }
    }
}
