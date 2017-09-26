package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Broker;

/**
 * A SystemEvent involving a broker.
 */
public class AbstractBrokerEvent extends AbstractHostEvent<Broker> {

    public AbstractBrokerEvent(Broker host) {
        super(host);
    }

    /**
     * Deprecated: use {@link #getHost()} instead.
     */
    @Deprecated
    public Broker getBroker() {
        return getHost();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{broker=" + getHost() + '}';
    }
}
