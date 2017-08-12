package at.ac.tuwien.dsg.emma.manager.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

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
                    network.addEdge(newNode, node);
                }
            }
        }

        onUpdate();
    }

    public void remove(Client info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public void remove(Broker info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public Network getNetwork() {
        return network;
    }

    private void onUpdate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated Network: {}", network);
        }
    }
}
