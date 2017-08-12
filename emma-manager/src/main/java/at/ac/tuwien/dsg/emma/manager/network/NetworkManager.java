package at.ac.tuwien.dsg.emma.manager.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Graph;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.manager.network.graph.UndirectedGraph;

/**
 * NetworkManager.
 */
@Component
public class NetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);

    private final Graph network;

    public NetworkManager() {
        this.network = new UndirectedGraph();
    }

    public void add(BrokerInfo info) {
        synchronized (network) {
            if (network.findNode(info.getId()).isPresent()) {
                return;
            }
            Node<HostInfo> newNode = new Node<>(info.getId(), info);
            network.addNode(newNode);

            for (Node<HostInfo> node : network.getNodes()) {
                if (node != newNode) {
                    Edge edge = network.addEdge(newNode, node);
                    edge.setValue(new Metrics());
                }
            }
        }

        onUpdate();
    }

    public void add(ClientInfo info) {
        synchronized (network) {
            if (network.findNode(info.getId()).isPresent()) {
                return;
            }
            Node<ClientInfo> newNode = new Node<>(info.getId(), info);
            network.addNode(newNode);

            for (Node node : network.getNodes()) {
                if (node.getValue() instanceof BrokerInfo) {
                    network.addEdge(newNode, node);
                }
            }
        }

        onUpdate();
    }

    public void remove(ClientInfo info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public void remove(BrokerInfo info) {
        synchronized (network) {
            network.findNode(info.getId()).ifPresent(network::removeNode);
        }

        onUpdate();
    }

    public Graph getNetwork() {
        return network;
    }

    private void onUpdate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated Network: {}", network);
        }
    }
}
