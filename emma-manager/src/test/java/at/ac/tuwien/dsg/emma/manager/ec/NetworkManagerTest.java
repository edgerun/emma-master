package at.ac.tuwien.dsg.emma.manager.ec;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.network.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.network.ClientInfo;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

/**
 * NetworkManagerTest.
 */
public class NetworkManagerTest {
    @Test
    public void updateMetrics_setsMetricsCorrectly() throws Exception {
        NetworkManager manager = new NetworkManager();

        manager.add(new BrokerInfo("10.0.0.1", 1001));
        manager.add(new BrokerInfo("10.0.0.2", 1002));
        manager.add(new ClientInfo("10.0.0.3", 1003));

        manager.updateMetrics(new BrokerInfo("10.0.0.1", 1001), "cpu", 42.0);

        Node node;

        node = manager.getNetwork().getNodes().find("10.0.0.1:1001").orElseThrow(NoSuchElementException::new);
        assertEquals(42.0, node.getMetrics().get("cpu"), 0);

        node = manager.getNetwork().getNodes().find("10.0.0.2:1002").orElseThrow(NoSuchElementException::new);
        assertEquals(0, node.getMetrics().size());

        node = manager.getNetwork().getNodes().find("10.0.0.3:1003").orElseThrow(NoSuchElementException::new);
        assertEquals(0, node.getMetrics().size());
    }
}