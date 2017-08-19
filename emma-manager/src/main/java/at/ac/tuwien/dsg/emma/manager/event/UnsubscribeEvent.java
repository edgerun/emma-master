package at.ac.tuwien.dsg.emma.manager.event;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * UnsubscribeEvent.
 */
public class UnsubscribeEvent extends AbstractBrokerEvent {
    private String topic;

    public UnsubscribeEvent(Broker broker, String topic) {
        super(broker);
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
