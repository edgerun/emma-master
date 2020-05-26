package io.edgerun.emma.controller.event;

import io.edgerun.emma.controller.model.Host;

/**
 * AbstractHostEvent.
 */
public class AbstractHostEvent<T extends Host> implements SystemEvent {
    private T host;

    public AbstractHostEvent(T host) {
        this.host = host;
    }

    public T getHost() {
        return host;
    }
}
