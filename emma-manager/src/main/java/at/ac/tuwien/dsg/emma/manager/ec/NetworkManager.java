package at.ac.tuwien.dsg.emma.manager.ec;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.broker.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.overlay.CompleteGraph;
import at.ac.tuwien.dsg.emma.manager.overlay.Node;
import at.ac.tuwien.dsg.emma.manager.overlay.NodeType;

/**
 * NetworkManager.
 */
@Component
public class NetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);

    private CompleteGraph network;

    public NetworkManager() {
        this.network = new CompleteGraph();
    }

    public void remove(ClientInfo info) {
        Node node = new Node(info.getId(), NodeType.GATEWAY);
        network.remove(node);

        onUpdate();
    }

    public void add(ClientInfo info) {
        Node node = new Node(info.getId(), NodeType.GATEWAY);
        network.add(node);

        onUpdate();
    }

    public void add(BrokerInfo info) {
        Node node = new Node(info.getId(), NodeType.BROKER);
        network.add(node);

        onUpdate();
    }

    public void remove(BrokerInfo info) {
        Node node = new Node(info.getId(), NodeType.BROKER);
        network.remove(node);

        onUpdate();
    }

    public void updateMetrics(NodeInfo nodeInfo, String metric, double value) {
        network.getNodes()
                .find(nodeInfo.getId())
                .ifPresent(n -> n.getMetrics().put(metric, value));
    }

    public Double getMetric(NodeInfo nodeInfo, String metric) {
        return network.getNodes()
                .find(nodeInfo.getId())
                .orElseThrow(() -> new NoSuchElementException("No such element: " + nodeInfo.getId()))
                .getMetrics()
                .get(metric);
    }

    public CompleteGraph getNetwork() {
        return network;
    }

    private void onUpdate() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updated Network: {}", network);
        }
    }
}
