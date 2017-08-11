package at.ac.tuwien.dsg.emma.manager.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.manager.network.graph.Graph;

/**
 * NetworkManager.
 */
@Component
public class NetworkManager {

    private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);

    private Graph network;

    public NetworkManager() {
        //        this.network = new CompleteGraph();
    }

    public void remove(ClientInfo info) {
        onUpdate();
    }

    public void add(ClientInfo info) {
        onUpdate();
    }

    public void add(BrokerInfo info) {
        onUpdate();
    }

    public void remove(BrokerInfo info) {
        onUpdate();
    }

    public void updateMetrics(HostInfo hostInfo, String metric, double value) {

    }

    public Double getMetric(HostInfo hostInfo, String metric) {
        return null;
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
