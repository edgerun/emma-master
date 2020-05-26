package io.edgerun.emma.controller.network.sel;

import java.util.Optional;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.controller.network.Network;
import io.edgerun.emma.controller.network.graph.Node;

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

}
