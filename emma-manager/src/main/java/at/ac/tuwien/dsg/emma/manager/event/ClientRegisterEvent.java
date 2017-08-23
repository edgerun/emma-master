package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Client;

/**
 * ClientRegisterEvent.
 */
public class ClientRegisterEvent extends AbstractClientEvent {
    public ClientRegisterEvent(Client host) {
        super(host);
    }
}
