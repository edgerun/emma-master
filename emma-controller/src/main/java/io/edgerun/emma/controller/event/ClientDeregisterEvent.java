package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Client;

/**
 * ClientDeregisterEvent.
 */
public class ClientDeregisterEvent extends AbstractClientEvent {
    public ClientDeregisterEvent(Client host) {
        super(host);
    }
}
