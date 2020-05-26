package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Client;

/**
 * AbstractClientEvent.
 */
public class AbstractClientEvent extends AbstractHostEvent<Client> {
    public AbstractClientEvent(Client host) {
        super(host);
    }
}
