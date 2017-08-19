package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * SubscribeEvent.
 */
public class SubscribeEvent extends AbstractBrokerEvent {

    private String topic;

    public SubscribeEvent(Broker broker, String topic) {
        super(broker);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
