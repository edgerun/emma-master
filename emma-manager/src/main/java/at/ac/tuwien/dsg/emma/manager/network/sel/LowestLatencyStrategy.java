package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.Comparator;
import java.util.Optional;

import at.ac.tuwien.dsg.emma.manager.network.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.network.ClientInfo;
import at.ac.tuwien.dsg.emma.manager.network.HostInfo;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Graph;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

/**
 * LowestLatencyStrategy.
 */
public class LowestLatencyStrategy implements BrokerSelectionStrategy {

    private LatencyComparator comparator = new LatencyComparator();

    @Override
    public BrokerInfo select(ClientInfo client, Graph graph) {
        Node<HostInfo> node = graph.getNode(client.getId());

        if (node == null) {
            return null;
        }

        Optional<Node> best = graph.getEdges(node)
                .stream()
                .map(e -> (Edge<Metrics>) e)
                .min(comparator)
                .map(e -> e.opposite(node));

        return (BrokerInfo) best
                .orElseThrow(() -> new IllegalStateException("Should have at least one connected broker"))
                .getValue();
    }

    private static class LatencyComparator implements Comparator<Edge<Metrics>> {

        @Override
        public int compare(Edge<Metrics> o1, Edge<Metrics> o2) {
            return Double.compare(getLatency(o1), getLatency(o2));
        }

        private double getLatency(Edge<Metrics> node) {
            return node.getValue().getTable().getOrDefault("lat", Double.MAX_VALUE);
        }
    }
}
