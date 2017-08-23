package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Host;

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
