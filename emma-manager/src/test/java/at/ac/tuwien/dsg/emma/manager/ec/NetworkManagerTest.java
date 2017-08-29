package at.ac.tuwien.dsg.emma.manager.ec;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
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

        manager.add(new Broker("10.0.0.1", 1001));
        manager.add(new Broker("10.0.0.2", 1002));
        manager.add(new Client("10.0.0.3", 1003));

        Node<Host> node;
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

        manager.add(new Broker("10.0.0.1", 1001));
        manager.add(new Broker("10.0.0.2", 1002));
        manager.add(new Broker("10.0.0.3", 1003));
        manager.add(new Client("10.0.0.4", 1004));
        manager.add(new Client("10.0.0.5", 1005));

        Graph network = manager.getNetwork();

        Collection<Edge> edges = network.getEdges();

        assertEquals(9, edges.size());

        Collection<Node> neighbours = network.getNeighbours(network.getNode("10.0.0.4:1004"));
        assertEquals(3, neighbours.size());
        for (Node neighbour : neighbours) {
            assertEquals(Broker.class, neighbour.getValue().getClass());
        }
    }

    @Test
    public void removeNode_removesEdgesCorrectly() throws Exception {
        NetworkManager manager = new NetworkManager();

        manager.add(new Broker("10.0.0.1", 1001));
        manager.add(new Broker("10.0.0.2", 1002));
        manager.add(new Broker("10.0.0.3", 1003));

        manager.remove(new Broker("10.0.0.1", 1001));

        assertEquals(1, manager.getNetwork().getEdges().size());
    }


    @Test
    public void getClientNodes_returnsOnlyClientNodes() throws Exception {
        NetworkManager manager = new NetworkManager();

        Client c1 = new Client("10.0.0.4", 1004);
        Client c2 = new Client("10.0.0.5", 1005);

        manager.add(new Broker("10.0.0.1", 1001));
        manager.add(new Broker("10.0.0.3", 1003));
        manager.add(c1);
        manager.add(c2);


        Collection<Node<Client>> clientNodes = manager.getNetwork().getClientNodes();
        assertEquals(2, clientNodes.size());
        List<Client> clients = clientNodes.stream().map(n -> n.getValue()).collect(Collectors.toList());
        assertThat(clients, hasItem(c1));
        assertThat(clients, hasItem(c2));
    }
}