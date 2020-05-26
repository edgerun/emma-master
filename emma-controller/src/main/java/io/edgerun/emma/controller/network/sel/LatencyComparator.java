package io.edgerun.emma.controller.network.sel;

import java.util.Comparator;

import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.controller.network.Link;
import io.edgerun.emma.controller.network.graph.Edge;
import io.edgerun.emma.util.LongWindow;

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
