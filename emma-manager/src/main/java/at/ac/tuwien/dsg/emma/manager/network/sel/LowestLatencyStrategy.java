package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.Comparator;
import java.util.Optional;

import at.ac.tuwien.dsg.emma.manager.network.Broker;
import at.ac.tuwien.dsg.emma.manager.network.Client;
import at.ac.tuwien.dsg.emma.manager.network.Host;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

/**
 * LowestLatencyStrategy.
 */
public class LowestLatencyStrategy implements BrokerSelectionStrategy {

    private LatencyComparator comparator = new LatencyComparator();

    @Override
    public Broker select(Client client, Network graph) {
        Node<Host> node = graph.getNode(client.getId());

        if (node == null) {
            return null;
        }

        Optional<Node> best = graph.getEdges(node)
                .stream()
                .min(comparator)
                .map(e -> e.opposite(node));

        return (Broker) best
                .orElseThrow(() -> new IllegalStateException("Should have at least one connected broker"))
                .getValue();
    }

    private static class LatencyComparator implements Comparator<Edge<Host, Metrics>> {

        @Override
        public int compare(Edge<Host, Metrics> o1, Edge<Host, Metrics> o2) {
            return Double.compare(getLatency(o1), getLatency(o2));
        }

        private double getLatency(Edge<Host, Metrics> node) {
            return node.getValue().getTable().getOrDefault("lat", Double.MAX_VALUE);
        }
    }
}
