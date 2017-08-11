package at.ac.tuwien.dsg.emma.manager.network;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public String toString() {
        return table.toString();
    }
}
