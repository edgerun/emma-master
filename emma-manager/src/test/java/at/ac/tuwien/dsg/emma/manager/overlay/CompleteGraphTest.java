package at.ac.tuwien.dsg.emma.manager.overlay;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.network.graph.CompleteGraph;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.manager.network.graph.NodeSet;
import at.ac.tuwien.dsg.emma.manager.network.graph.NodeType;

/**
 * CompleteGraphTest.
 */
public class CompleteGraphTest {
    @Test
    public void add_connectsNodesCorrectly() throws Exception {
        CompleteGraph graph = new CompleteGraph();

        graph.add(new Node("br0", NodeType.BROKER));
        assertEquals(1, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());

        graph.add(new Node("br1", NodeType.BROKER));
        assertEquals(2, graph.getNodes().size());
        assertEquals(1, graph.getEdges().size());

        graph.add(new Node("gw1", NodeType.GATEWAY));
        assertEquals(3, graph.getNodes().size());
        assertEquals(3, graph.getEdges().size());

        System.out.println(graph);
    }

    @Test
    public void getNeighbours_returnsCorrectNodes() throws Exception {
        CompleteGraph graph = new CompleteGraph();

        Node br0 = new Node("br0", NodeType.BROKER);
        Node br1 = new Node("br1", NodeType.BROKER);
        Node br2 = new Node("br2", NodeType.BROKER);

        graph.add(br0);
        graph.add(br1);
        graph.add(br2);

        NodeSet neigh0 = graph.getNeighbours(br0);
        assertThat(neigh0, hasItem(br1));
        assertThat(neigh0, hasItem(br2));
        assertEquals(2, neigh0.size());

        NodeSet neigh1 = graph.getNeighbours(br1);
        assertThat(neigh1, hasItem(br0));
        assertThat(neigh1, hasItem(br2));
        assertEquals(2, neigh1.size());

        NodeSet neigh2 = graph.getNeighbours(br2);
        assertThat(neigh2, hasItem(br0));
        assertThat(neigh2, hasItem(br1));
        assertEquals(2, neigh2.size());
    }

    @Test
    public void remove_updatesGraphCorrectly() throws Exception {
        CompleteGraph graph = new CompleteGraph();

        Node br0 = new Node("br0", NodeType.BROKER);
        Node br1 = new Node("br1", NodeType.BROKER);
        Node br2 = new Node("br2", NodeType.BROKER);

        graph.add(br0);
        graph.add(br1);
        graph.add(br2);

        graph.remove(br2);
        assertEquals(2, graph.getNodes().size());
        assertEquals(1, graph.getEdges().size());

        Edge edge = graph.getEdges().iterator().next();
        assertTrue(edge.contains(br0));
        assertTrue(edge.contains(br1));

        graph.remove(br1);
        assertEquals(1, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());

        graph.remove(br0);
        assertEquals(0, graph.getNodes().size());
        assertEquals(0, graph.getEdges().size());
    }
}
