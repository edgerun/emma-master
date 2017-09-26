package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Broker;

/**
 * BrokerConnectEvent.
 */
public class BrokerConnectEvent extends AbstractBrokerEvent {

    public BrokerConnectEvent(Broker broker) {
        super(broker);
    }
}
