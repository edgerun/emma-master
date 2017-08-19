package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * A SystemEvent involving a broker.
 */
public class AbstractBrokerEvent implements SystemEvent {

    private Broker broker;

    public AbstractBrokerEvent(Broker broker) {
        this.broker = broker;
    }

    public Broker getBroker() {
        return broker;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{broker=" + broker + '}';
    }
}
