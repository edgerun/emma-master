package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.Comparator;
import java.util.Optional;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.util.LongWindow;

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

        Optional<Node<Host>> best = graph.getEdges(node)
                .stream()
                .min(comparator)
                .map(e -> e.opposite(node));

        return (Broker) best
                .orElseThrow(() -> new IllegalStateException("Should have at least one connected broker"))
                .getValue();
    }

    private static class LatencyComparator implements Comparator<Edge<Host, Link>> {

        @Override
        public int compare(Edge<Host, Link> o1, Edge<Host, Link> o2) {
            return Double.compare(getLatency(o1), getLatency(o2));
        }

        private double getLatency(Edge<Host, Link> node) {
            LongWindow latency = node.getValue().getLatency();

            return (latency == null || latency.count() < 1) ? Double.MAX_VALUE : latency.average();
        }
    }
}
