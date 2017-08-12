package at.ac.tuwien.dsg.emma.manager.network.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.network.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.network.ClientInfo;
import at.ac.tuwien.dsg.emma.manager.network.HostInfo;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;
import at.ac.tuwien.dsg.emma.manager.network.sel.LowestLatencyStrategy;

/**
 * GraphAlgorithmsTest.
 */
public class GraphAlgorithmsTest {

    Node<HostInfo> b1;
    Node<HostInfo> b2;
    Node<HostInfo> b3;
    Node<HostInfo> b4;
    Node<HostInfo> c1;
    Node<HostInfo> c2;

    Graph graph;

    @Before
    public void setUp() throws Exception {
        graph = new UndirectedGraph();

        b1 = new Node<>("10.0.0.1:1883", new BrokerInfo("10.0.0.1", 1883));
        b2 = new Node<>("10.0.0.2:1883", new BrokerInfo("10.0.0.2", 1883));
        b3 = new Node<>("10.0.0.3:1883", new BrokerInfo("10.0.0.3", 1883));
        b4 = new Node<>("10.0.0.4:1883", new BrokerInfo("10.0.0.4", 1883));
        c1 = new Node<>("10.0.1.1:1883", new ClientInfo("10.0.1.1", 1883));
        c2 = new Node<>("10.0.1.2:1883", new ClientInfo("10.0.1.2", 1883));

        graph.addNode(b1);
        graph.addNode(b2);
        graph.addNode(b3);
        graph.addNode(b4);
        graph.addNode(c1);
        graph.addNode(c2);

        graph.addEdge(b1, b2).setValue(new Metrics().set("lat", 100));
        graph.addEdge(b1, b3).setValue(new Metrics().set("lat", 150));
        graph.addEdge(b1, b4).setValue(new Metrics().set("lat", 150));
        graph.addEdge(b2, b3).setValue(new Metrics().set("lat", 10));
        graph.addEdge(b2, b4).setValue(new Metrics().set("lat", 20));
        graph.addEdge(b3, b4).setValue(new Metrics().set("lat", 5));

        graph.addEdge(c1, b1).setValue(new Metrics().set("lat", 10));
        graph.addEdge(c1, b2).setValue(new Metrics().set("lat", 150));
        graph.addEdge(c1, b3).setValue(new Metrics().set("lat", 200));
        graph.addEdge(c1, b4).setValue(new Metrics().set("lat", 200));
        graph.addEdge(c2, b1).setValue(new Metrics().set("lat", 160));
        graph.addEdge(c2, b2).setValue(new Metrics().set("lat", 50));
        graph.addEdge(c2, b3).setValue(new Metrics().set("lat", 5));
        graph.addEdge(c2, b4).setValue(new Metrics().set("lat", 5));
    }

    @Test
    public void testLowestLatencySelection() throws Exception {
        LowestLatencyStrategy strategy = new LowestLatencyStrategy();

        BrokerInfo broker = strategy.select((ClientInfo) c1.getValue(), graph);

        assertEquals(broker, b1.getValue());
    }

}
