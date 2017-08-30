package at.ac.tuwien.dsg.emma.manager.network.balancing;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;

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
