package at.ac.tuwien.dsg.emma.controller.network.balancing;

import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.Client;

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
