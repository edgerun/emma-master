package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Broker;

/**
 * BrokerConnectEvent.
 */
public class BrokerConnectEvent extends AbstractBrokerEvent {

    public BrokerConnectEvent(Broker broker) {
        super(broker);
    }
}
