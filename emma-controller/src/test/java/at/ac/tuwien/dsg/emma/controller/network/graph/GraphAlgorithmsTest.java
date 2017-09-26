package at.ac.tuwien.dsg.emma.controller.network.graph;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.Host;
import at.ac.tuwien.dsg.emma.controller.network.Link;
import at.ac.tuwien.dsg.emma.controller.network.Network;
import at.ac.tuwien.dsg.emma.controller.network.balancing.LatencyGrouping;
import at.ac.tuwien.dsg.emma.controller.network.sel.LowLoadAndLatencyStrategy;
import at.ac.tuwien.dsg.emma.controller.network.sel.LowestLatencyStrategy;

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

        graph.addEdge(b1, b2).setValue(new Link().addLatency(100));
        graph.addEdge(b1, b3).setValue(new Link().addLatency(150));
        graph.addEdge(b1, b4).setValue(new Link().addLatency(150));
        graph.addEdge(b2, b3).setValue(new Link().addLatency(10));
        graph.addEdge(b2, b4).setValue(new Link().addLatency(20));
        graph.addEdge(b3, b4).setValue(new Link().addLatency(5));
        graph.addEdge(c1, b1).setValue(new Link().addLatency(10));
        graph.addEdge(c1, b2).setValue(new Link().addLatency(150));
        graph.addEdge(c1, b3).setValue(new Link().addLatency(200));
        graph.addEdge(c1, b4).setValue(new Link().addLatency(200));
        graph.addEdge(c2, b1).setValue(new Link().addLatency(160));
        graph.addEdge(c2, b2).setValue(new Link().addLatency(50));
        graph.addEdge(c2, b3).setValue(new Link().addLatency(2));
        graph.addEdge(c2, b4).setValue(new Link().addLatency(1));
    }

    @Test
    public void testLowestLatencySelection() throws Exception {
        LowestLatencyStrategy strategy = new LowestLatencyStrategy();

        Broker broker = strategy.select((Client) c1.getValue(), graph);

        assertEquals(broker, b1.getValue());
    }

    @Test
    public void testLowLoadAndLatencyStrategy() throws Exception {
        LowLoadAndLatencyStrategy strategy = new LowLoadAndLatencyStrategy();

        // b3 and b4 are are in the same bucket

        // latency = 160
        b1.getValue().getMetrics().set("processors", 4);
        b1.getValue().getMetrics().set("load", 1);

        // latecny = 50
        b2.getValue().getMetrics().set("processors", 4);
        b2.getValue().getMetrics().set("load", 1);

        // latency = 2
        b3.getValue().getMetrics().set("processors", 2);
        b3.getValue().getMetrics().set("load", 1); // effective load will be 0.5

        // latency = 1
        b4.getValue().getMetrics().set("processors", 1);
        b4.getValue().getMetrics().set("load", 1); // effective load will be 1


        Broker broker = strategy.select((Client) c2.getValue(), graph);

        assertEquals(broker, b3.getValue());
    }

    @Test
    public void latencyGrouping() throws Exception {
        LatencyGrouping lg = new LatencyGrouping();

        Collection<Edge<Host, Link>> group = lg.getLowestLatencyGroup(c2, graph);
        List<Node<Host>> collect = group.stream().map(e -> e.opposite(c2)).collect(Collectors.toList());

        assertEquals(2, group.size());
        assertThat(collect, hasItems(b3, b4));
    }

}
