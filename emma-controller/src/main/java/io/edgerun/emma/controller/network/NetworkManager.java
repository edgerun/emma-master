package io.edgerun.emma.controller.network;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.model.Host;
import io.edgerun.emma.controller.network.graph.Edge;
import io.edgerun.emma.controller.network.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * NetworkManager.
 */
@Component
public class NetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);

    private final Network network;

    public NetworkManager() {
        this.network = new Network();
    }

    public void add(Broker info) {
        synchronized (network) {
            if (network.findNode(info.getId()).isPresent()) {
                return;
            }
            Node<Host> newNode = new Node<>(info.getId(), info);
            network.addNode(newNode);

            for (Node<Host> node : network.getNodes()) {
                if (node != newNode) {
                    Edge<Host, Link> edge = network.addEdge(newNode, node);
                    edge.setValue(new Link());
                }
            }
        }

        onUpdate();
    }

    public void add(Client info) {
        synchronized (network) {
            if (network.findNode(info.getId()).isPresent()) {
                return;
            }
            Node<Client> newNode = new Node<>(info.getId(), info);
            network.addNode(newNode);

            for (Node node : network.getNodes()) {
                if (node.getValue() instanceof Broker) {
                    Edge<Host, Link> edge = network.addEdge(newNode, node);
                    edge.setValue(new Link());
                }
            }
        }

        onUpdate();
    }

    public Link getLink(Host source, Host target) {
        synchronized (network) {
            Edge<Host, Link> edge = getEdge(source, target);
            return edge != null ? edge.getValue() : null;
        }
    }

    public void remove(Client info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public Edge<Host, Link> getEdge(Host source, Host target) {
        Node sourceNode = network.getNode(source.getId());
        Node targetNode = network.getNode(target.getId());

        if (sourceNode == null || targetNode == null) {
            return null;
        }

        Edge<Host, Link> edge = network.getEdge(sourceNode, targetNode);
        if (edge == null) {
            return null;
        }

        return edge;
    }

    public void remove(Broker info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public Network getNetwork() {
        synchronized (network) {
            return network;
        }
    }

    private void onUpdate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated Network: {}", network);
        }
    }
}
