package at.ac.tuwien.dsg.emma.manager.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Metrics.
 */
public class Metrics {

    private final Map<String, Double> table;

    public Metrics() {
        this.table = new HashMap<>();
    }

    public Map<String, Double> getTable() {
        return table;
    }

    public Metrics set(String k, double v) {
        table.put(k, v);
        return this;
    }

    public Double get(String k) {
        return table.get(k);
    }

    public Optional<Double> maybe(String k) {
        return Optional.ofNullable(table.get(k));
    }

    public void update(String k, BiFunction<String, Optional<Double>, Double> updateFunction) {
        Optional<Double> oldValue = maybe(k);
        Double newValue = updateFunction.apply(k, oldValue);

        table.put(k, newValue);
    }

    @Override
    public String toString() {
        return table.toString();
    }
}
