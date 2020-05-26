package io.edgerun.emma.controller.network;

import java.util.Collection;
import java.util.stream.Collectors;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.controller.network.graph.Node;
import io.edgerun.emma.controller.network.graph.UndirectedGraph;

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
