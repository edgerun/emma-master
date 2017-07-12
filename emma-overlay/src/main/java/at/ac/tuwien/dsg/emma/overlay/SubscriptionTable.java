package at.ac.tuwien.dsg.emma.overlay;

import java.util.Collection;

/**
 * SubscriptionTable.
 */
public interface SubscriptionTable {

    Collection<Destination> getDestinations(Topic topic);

    void add(Topic topic, Destination destination);

    void remove(Topic topic, Destination destination);
}
