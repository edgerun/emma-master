package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;

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
