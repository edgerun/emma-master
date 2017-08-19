package at.ac.tuwien.dsg.emma.manager.service.sub;

import java.util.Objects;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * Subscription.
 */
public class Subscription {

    private Broker broker;
    private String filter;

    public Subscription(Broker broker, String filter) {
        this.broker = broker;
        this.filter = filter;
    }

    public Broker getBroker() {
        return broker;
    }

    public void setBroker(Broker broker) {
        this.broker = broker;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Subscription that = (Subscription) o;
        return Objects.equals(broker, that.broker) &&
                Objects.equals(filter, that.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broker, filter);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "broker=" + broker +
                ", filter='" + filter + '\'' +
                '}';
    }
}
