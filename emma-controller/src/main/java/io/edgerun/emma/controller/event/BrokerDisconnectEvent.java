package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Broker;

/**
 * BrokerDisconnectEvent.
 */
public class BrokerDisconnectEvent extends AbstractBrokerEvent {
    public BrokerDisconnectEvent(Broker broker) {
        super(broker);
    }
}
