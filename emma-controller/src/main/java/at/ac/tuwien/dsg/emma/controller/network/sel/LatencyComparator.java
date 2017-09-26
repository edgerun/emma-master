package at.ac.tuwien.dsg.emma.controller.network.sel;

import java.util.Comparator;

import at.ac.tuwien.dsg.emma.controller.model.Host;
import at.ac.tuwien.dsg.emma.controller.network.Link;
import at.ac.tuwien.dsg.emma.controller.network.graph.Edge;
import at.ac.tuwien.dsg.emma.util.LongWindow;

/**
 * LatencyComparator.
 */
public class LatencyComparator implements Comparator<Edge<Host, Link>> {

    @Override
    public int compare(Edge<Host, Link> o1, Edge<Host, Link> o2) {
        return Double.compare(getLatency(o1), getLatency(o2));
    }

    private double getLatency(Edge<Host, Link> node) {
        LongWindow latency = node.getValue().getLatency();

        return (latency == null || latency.count() < 1) ? Double.MAX_VALUE : latency.average();
    }
}
