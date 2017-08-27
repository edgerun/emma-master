package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.Comparator;

import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;

/**
 * LoadComparator.
 */
public final class LoadComparator implements Comparator<Host> {

    @Override
    public int compare(Host o1, Host o2) {
        return Double.compare(getActualLoad(o1), getActualLoad(o2));
    }

    private double getActualLoad(Host host) {
        Metrics metrics = host.getMetrics();

        Double load = metrics.get("load");

        if (load == null) {
            return Double.MAX_VALUE;
        }

        return load / metrics.maybe("processors").orElse(1d);
    }
}
