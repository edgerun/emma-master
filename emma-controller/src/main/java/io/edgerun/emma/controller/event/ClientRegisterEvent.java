package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Client;

/**
 * ClientRegisterEvent.
 */
public class ClientRegisterEvent extends AbstractClientEvent {
    public ClientRegisterEvent(Client host) {
        super(host);
    }
}
