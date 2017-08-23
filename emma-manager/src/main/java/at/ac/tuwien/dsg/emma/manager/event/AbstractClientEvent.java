package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Client;

/**
 * AbstractClientEvent.
 */
public class AbstractClientEvent extends AbstractHostEvent<Client> {
    public AbstractClientEvent(Client host) {
        super(host);
    }
}
