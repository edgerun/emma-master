package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Host;

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
