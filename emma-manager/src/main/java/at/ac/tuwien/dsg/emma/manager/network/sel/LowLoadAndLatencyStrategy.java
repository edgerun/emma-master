package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.util.LongWindow;

/**
 * LowLoadAndLatencyStrategy.
 */
public class LowLoadAndLatencyStrategy implements BrokerSelectionStrategy {

    private static final int[] DEFAULT_BUCKETS = {2, 5, 10, 20, 50, 100, 200, 500, 1000};

    private int[] buckets;

    public LowLoadAndLatencyStrategy() {
        this(DEFAULT_BUCKETS);
    }

    public LowLoadAndLatencyStrategy(int[] buckets) {
        this.buckets = buckets;
    }

    @Override
    public Broker select(Client client, Network network) {
        Node<Host> clientNode = network.getNode(client.getId());

        Collection<Edge<Host, Link>> edges = network.getEdges(clientNode);

        edges = filterLowestLatencyBucket(edges);

        return getLowestLoad(clientNode, edges);
    }

    private Collection<Edge<Host, Link>> filterLowestLatencyBucket(Collection<Edge<Host, Link>> edges) {
        int lowestBucket = buckets.length - 1;
        List<Edge<Host, Link>> lowest = new ArrayList<>();

        for (Edge<Host, Link> edge : edges) {
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

    private Broker getLowestLoad(Node<Host> clientNode, Collection<Edge<Host, Link>> edges) {
        // TODO this calls for a threshold or a way for the ReconnectEngine to decide whether

        return (Broker) edges.stream()
                .map(e -> e.opposite(clientNode))
                .map(n -> n.getValue())
                .min(new LoadComparator())
                .orElseThrow(() -> new IllegalStateException("Should have at least one broker"));
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
