package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * BrokerConnectEvent.
 */
public class BrokerConnectEvent extends AbstractBrokerEvent {

    public BrokerConnectEvent(Broker broker) {
        super(broker);
    }
}
