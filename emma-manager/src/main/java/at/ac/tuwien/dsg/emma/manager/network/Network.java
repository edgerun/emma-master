package at.ac.tuwien.dsg.emma.manager.network;

import java.util.Collection;
import java.util.stream.Collectors;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.manager.network.graph.UndirectedGraph;

/**
 * Network.
 */
public class Network extends UndirectedGraph<Host, Link> {

    @SuppressWarnings("unchecked")
    public Collection<Node<Client>> getClientNodes() {
        return (Collection) getNodes().stream()
                .filter(n -> n.getValue() instanceof Client)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Collection<Node<Broker>> getBrokerNodes() {
        return (Collection) getNodes().stream()
                .filter(n -> n.getValue() instanceof Broker)
                .collect(Collectors.toList());
    }

}
