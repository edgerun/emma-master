package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Client;

/**
 * AbstractClientEvent.
 */
public class AbstractClientEvent extends AbstractHostEvent<Client> {
    public AbstractClientEvent(Client host) {
        super(host);
    }
}
