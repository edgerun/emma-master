package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Client;

/**
 * ClientDeregisterEvent.
 */
public class ClientDeregisterEvent extends AbstractClientEvent {
    public ClientDeregisterEvent(Client host) {
        super(host);
    }
}
