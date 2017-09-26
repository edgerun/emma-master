package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Client;

/**
 * ClientDeregisterEvent.
 */
public class ClientDeregisterEvent extends AbstractClientEvent {
    public ClientDeregisterEvent(Client host) {
        super(host);
    }
}
