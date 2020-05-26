package io.edgerun.emma.controller.network.balancing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.controller.network.Link;
import io.edgerun.emma.controller.network.Network;
import io.edgerun.emma.controller.network.graph.Edge;
import io.edgerun.emma.controller.network.graph.Node;
import io.edgerun.emma.util.LongWindow;

/**
 * LatencyGrouping.
 */
public class LatencyGrouping {

    private static final int[] DEFAULT_BUCKETS = {2, 5, 10, 20, 50, 100, 200, 500, 1000};

    private int[] buckets;

    public LatencyGrouping() {
        this(DEFAULT_BUCKETS);
    }

    public LatencyGrouping(int[] buckets) {
        this.buckets = buckets;
    }

    public Map<Integer, List<Edge<Host, Link>>> group(Network network) {
        return network.getEdges()
                .stream()
                .collect(Collectors.groupingBy(e -> getLatencyGroup(getLatency(e))));
    }

    public Collection<Edge<Host, Link>> getLowestLatencyGroup(Node<? extends Host> origin, Network network) {
        int lowestBucket = buckets.length - 1;
        List<Edge<Host, Link>> lowest = new ArrayList<>();

        for (Edge<Host, Link> edge : network.getEdges(origin)) {
            int latencyGroup = getLatencyGroup(getLatency(edge));

            if (latencyGroup > lowestBucket) {
                continue;
            }
            if (latencyGroup < lowestBucket) {
                lowestBucket = latencyGroup;
                lowest.clear();
            }

            lowest.add(edge);
        }

        return lowest;
    }

    private double getLatency(Edge<Host, Link> edge) {
        LongWindow latency = edge.getValue().getLatency();
        return (latency == null || latency.count() < 1) ? Double.MAX_VALUE : latency.average();
    }

    public int getLatencyGroup(double value) {
        for (int i = 0; i < buckets.length; i++) {
            if (value <= buckets[i]) {
                return i;
            }
        }
        return buckets.length - 1;
    }
}
