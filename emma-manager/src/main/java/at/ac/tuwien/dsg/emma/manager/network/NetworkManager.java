package at.ac.tuwien.dsg.emma.manager.network;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.network.graph.CompleteGraph;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.manager.network.graph.NodeType;

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

    public void updateMetrics(HostInfo hostInfo, String metric, double value) {
        network.getNodes()
                .find(hostInfo.getId())
                .ifPresent(n -> n.getMetrics().put(metric, value));
    }

    public Double getMetric(HostInfo hostInfo, String metric) {
        return network.getNodes()
                .find(hostInfo.getId())
                .orElseThrow(() -> new NoSuchElementException("No such element: " + hostInfo.getId()))
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
