package io.edgerun.emma.controller.network.balancing;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;

/**
 * BalancingOperation.
 */
public class BalancingOperation {

    private Client client;
    private Broker target;

    public BalancingOperation(Client client, Broker target) {
        this.client = client;
        this.target = target;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Broker getTarget() {
        return target;
    }

    public void setTarget(Broker target) {
        this.target = target;
    }
}
