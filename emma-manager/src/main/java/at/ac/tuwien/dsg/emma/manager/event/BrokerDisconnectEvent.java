package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * BrokerDisconnectEvent.
 */
public class BrokerDisconnectEvent extends AbstractBrokerEvent {
    public BrokerDisconnectEvent(Broker broker) {
        super(broker);
    }
}
