package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Broker;

/**
 * BrokerDisconnectEvent.
 */
public class BrokerDisconnectEvent extends AbstractBrokerEvent {
    public BrokerDisconnectEvent(Broker broker) {
        super(broker);
    }
}
