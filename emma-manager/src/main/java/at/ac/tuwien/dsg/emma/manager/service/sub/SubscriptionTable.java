package at.ac.tuwien.dsg.emma.manager.service.sub;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * SubscriptionTable.
 */
public class SubscriptionTable {

    private Set<Subscription> subscriptions;

    private Map<Broker, Map<String, Subscription>> brokerIndex;
    private Map<String, Map<Broker, Subscription>> filterIndex;

    public SubscriptionTable() {
        this.subscriptions = new HashSet<>();
        this.brokerIndex = new HashMap<>();
        this.filterIndex = new HashMap<>();
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Collection<Subscription> getSubscriptions(String filter) {
        return getSubscriptionTable(filter).map(Map::values).orElse(Collections.emptySet());
    }

    public Collection<Subscription> getSubscriptions(Broker broker) {
        return getSubscriptionTable(broker).map(Map::values).orElse(Collections.emptySet());
    }

    public Subscription getOrCreate(Broker broker, String filter) {
        Subscription subscription = new Subscription(broker, filter);

        if (subscriptions.contains(subscription)) {
            return brokerIndex.get(broker).get(filter);
        }

        subscriptions.add(subscription);
        onAfterAdd(subscription);
        return subscription;
    }

    public Subscription get(Broker broker, String filter) {
        return getSubscriptionTable(broker).map(m -> m.get(filter)).orElse(null);
    }

    public void remove(Subscription subscription) {
        subscriptions.remove(subscription);
        onAfterRemove(subscription);
    }

    private void onAfterAdd(Subscription subscription) {
        Broker broker = subscription.getBroker();
        String filter = subscription.getFilter();

        brokerIndex.computeIfAbsent(broker, (b) -> new HashMap<>()).put(filter, subscription);
        filterIndex.computeIfAbsent(filter, (f) -> new HashMap<>()).put(broker, subscription);
    }

    private void onAfterRemove(Subscription subscription) {
        Broker broker = subscription.getBroker();
        String filter = subscription.getFilter();

        getSubscriptionTable(broker).ifPresent(m -> m.remove(filter));
        getSubscriptionTable(filter).ifPresent(m -> m.remove(broker));
    }

    private Optional<Map<String, Subscription>> getSubscriptionTable(Broker broker) {
        return Optional.ofNullable(brokerIndex.get(broker));
    }

    private Optional<Map<Broker, Subscription>> getSubscriptionTable(String filter) {
        return Optional.ofNullable(filterIndex.get(filter));
    }

}
