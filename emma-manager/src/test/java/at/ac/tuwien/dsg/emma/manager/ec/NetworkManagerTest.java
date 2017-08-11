package at.ac.tuwien.dsg.emma.manager.ec;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.network.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.network.ClientInfo;
import at.ac.tuwien.dsg.emma.manager.network.HostInfo;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Graph;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;

/**
 * NetworkManagerTest.
 */
public class NetworkManagerTest {
    @Test
    public void add_createsNodesCorrectly() throws Exception {
        NetworkManager manager = new NetworkManager();

        manager.add(new BrokerInfo("10.0.0.1", 1001));
        manager.add(new BrokerInfo("10.0.0.2", 1002));
        manager.add(new ClientInfo("10.0.0.3", 1003));

        Node<HostInfo> node;
        node = manager.getNetwork().findNode("10.0.0.1:1001").orElseThrow(NoSuchElementException::new);
        assertEquals("10.0.0.1:1001", node.getValue().getId());

        node = manager.getNetwork().findNode("10.0.0.2:1002").orElseThrow(NoSuchElementException::new);
        assertEquals("10.0.0.2:1002", node.getValue().getId());

        node = manager.getNetwork().findNode("10.0.0.3:1003").orElseThrow(NoSuchElementException::new);
        assertEquals("10.0.0.3:1003", node.getValue().getId());
    }

    @Test
    public void add_createsEdgesCorrectly() throws Exception {
        NetworkManager manager = new NetworkManager();

        manager.add(new BrokerInfo("10.0.0.1", 1001));
        manager.add(new BrokerInfo("10.0.0.2", 1002));
        manager.add(new BrokerInfo("10.0.0.3", 1003));
        manager.add(new ClientInfo("10.0.0.4", 1004));
        manager.add(new ClientInfo("10.0.0.5", 1005));

        Graph network = manager.getNetwork();

        Collection<Edge> edges = network.getEdges();

        assertEquals(9, edges.size());

        Collection<Node> neighbours = network.getNeighbours(network.getNode("10.0.0.4:1004"));
        assertEquals(3, neighbours.size());
        for (Node neighbour : neighbours) {
            assertEquals(BrokerInfo.class, neighbour.getValue().getClass());
        }
    }

    @Test
    public void removeNode_removesEdgesCorrectly() throws Exception {
        NetworkManager manager = new NetworkManager();

        manager.add(new BrokerInfo("10.0.0.1", 1001));
        manager.add(new BrokerInfo("10.0.0.2", 1002));
        manager.add(new BrokerInfo("10.0.0.3", 1003));

        manager.remove(new BrokerInfo("10.0.0.1", 1001));

        assertEquals(1, manager.getNetwork().getEdges().size());
    }
}