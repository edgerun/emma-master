package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Client;

/**
 * ClientRegisterEvent.
 */
public class ClientRegisterEvent extends AbstractClientEvent {
    public ClientRegisterEvent(Client host) {
        super(host);
    }
}
