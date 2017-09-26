package at.ac.tuwien.dsg.emma.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.controller.event.SubscribeEvent;
import at.ac.tuwien.dsg.emma.controller.event.UnsubscribeEvent;
import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.BrokerRepository;

/**
 * SubscriptionController.
 */
@RestController
public class SubscriptionController {

    private static final Logger LOG = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private ApplicationEventPublisher systemEvents;

    @RequestMapping(value = "/broker/onSubscribe", method = RequestMethod.GET)
    public void onSubscribe(String id, String topic) {
        LOG.debug("/broker/onSubscribe({},{})", id, topic);

        Broker broker = brokerRepository.getById(id);

        if (broker == null) {
            LOG.warn("Broker with host {} not found", id);
            // FIXME
            return;
        }

        systemEvents.publishEvent(new SubscribeEvent(broker, topic));
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

        systemEvents.publishEvent(new UnsubscribeEvent(broker, topic));
    }

}
