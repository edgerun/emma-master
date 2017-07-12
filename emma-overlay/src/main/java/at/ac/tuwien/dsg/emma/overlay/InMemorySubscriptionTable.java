package at.ac.tuwien.dsg.emma.overlay;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * InMemorySubscriptionTable.
 */
public class InMemorySubscriptionTable implements SubscriptionTable {

    private Map<Topic, Set<Destination>> table;

    public InMemorySubscriptionTable() {
        table = new HashMap<>();
    }

    @Override
    public Collection<Destination> getDestinations(Topic topic) {
        return Collections.unmodifiableSet(table.computeIfAbsent(topic, k -> Collections.emptySet()));
    }

    @Override
    public void add(Topic topic, Destination destination) {
        getDestinations0(topic).add(destination);
    }

    @Override
    public void remove(Topic topic, Destination destination) {
        getDestinations0(topic).remove(destination);
    }

    public Set<Destination> getDestinations0(Topic topic) {
        return table.computeIfAbsent(topic, k -> new HashSet<>());
    }
}
