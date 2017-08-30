package at.ac.tuwien.dsg.emma.manager.network.sel;

import java.util.Collection;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.balancing.LatencyGrouping;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

/**
 * LowLoadAndLatencyStrategy.
 */
public class LowLoadAndLatencyStrategy implements BrokerSelectionStrategy {

    private LatencyGrouping grouping;

    public LowLoadAndLatencyStrategy() {
        this(new LatencyGrouping());
    }

    public LowLoadAndLatencyStrategy(LatencyGrouping grouping) {
        this.grouping = grouping;
    }

    @Override
    public Broker select(Client client, Network network) {
        Node<Host> clientNode = network.getNode(client.getId());

        Collection<Edge<Host, Link>> edges = grouping.getLowestLatencyGroup(clientNode, network);

        return getLowestLoad(clientNode, edges);
    }


    private Broker getLowestLoad(Node<Host> clientNode, Collection<Edge<Host, Link>> edges) {
        // TODO this calls for a threshold or a way for the ReconnectEngine to decide whether

        return (Broker) edges.stream()
                .map(e -> e.opposite(clientNode))
                .map(n -> n.getValue())
                .min(new LoadComparator())
                .orElseThrow(() -> new IllegalStateException("Should have at least one broker"));
    }


}
