package at.ac.tuwien.dsg.emma.manager.network.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.sel.LowestLatencyStrategy;

/**
 * GraphAlgorithmsTest.
 */
public class GraphAlgorithmsTest {

    Node<Host> b1;
    Node<Host> b2;
    Node<Host> b3;
    Node<Host> b4;
    Node<Host> c1;
    Node<Host> c2;

    Network graph;

    @Before
    public void setUp() throws Exception {
        graph = new Network();

        b1 = new Node<>("10.0.0.1:1883", new Broker("10.0.0.1", 1883));
        b2 = new Node<>("10.0.0.2:1883", new Broker("10.0.0.2", 1883));
        b3 = new Node<>("10.0.0.3:1883", new Broker("10.0.0.3", 1883));
        b4 = new Node<>("10.0.0.4:1883", new Broker("10.0.0.4", 1883));
        c1 = new Node<>("10.0.1.1:1883", new Client("10.0.1.1", 1883));
        c2 = new Node<>("10.0.1.2:1883", new Client("10.0.1.2", 1883));

        graph.addNode(b1);
        graph.addNode(b2);
        graph.addNode(b3);
        graph.addNode(b4);
        graph.addNode(c1);
        graph.addNode(c2);

        graph.addEdge(b1, b2).setValue(new Link().setLatency(100));
        graph.addEdge(b1, b3).setValue(new Link().setLatency(150));
        graph.addEdge(b1, b4).setValue(new Link().setLatency(150));
        graph.addEdge(b2, b3).setValue(new Link().setLatency(10));
        graph.addEdge(b2, b4).setValue(new Link().setLatency(20));
        graph.addEdge(b3, b4).setValue(new Link().setLatency(5));

        graph.addEdge(c1, b1).setValue(new Link().setLatency(10));
        graph.addEdge(c1, b2).setValue(new Link().setLatency(150));
        graph.addEdge(c1, b3).setValue(new Link().setLatency(200));
        graph.addEdge(c1, b4).setValue(new Link().setLatency(200));
        graph.addEdge(c2, b1).setValue(new Link().setLatency(160));
        graph.addEdge(c2, b2).setValue(new Link().setLatency(50));
        graph.addEdge(c2, b3).setValue(new Link().setLatency(5));
        graph.addEdge(c2, b4).setValue(new Link().setLatency(5));
    }

    @Test
    public void testLowestLatencySelection() throws Exception {
        LowestLatencyStrategy strategy = new LowestLatencyStrategy();

        Broker broker = strategy.select((Client) c1.getValue(), graph);

        assertEquals(broker, b1.getValue());
    }

}
