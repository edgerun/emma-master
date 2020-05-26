package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;

/**
 * ClientConnectEvent.
 */
public class ClientConnectEvent implements SystemEvent {

    private Client client;
    private Broker broker;

    public ClientConnectEvent(Client client, Broker broker) {
        this.client = client;
        this.broker = broker;
    }

    public Client getClient() {
        return client;
    }

    public Broker getBroker() {
        return broker;
    }
}
