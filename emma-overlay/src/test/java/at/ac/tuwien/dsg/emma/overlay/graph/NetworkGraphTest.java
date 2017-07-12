package at.ac.tuwien.dsg.emma.overlay.graph;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

/**
 * NetworkGraphTest.
 */
public class NetworkGraphTest {

    @Test
    public void test() throws Exception {
        NetworkGraph graph = new NetworkGraph();

        NetworkNode b1 = new NetworkNode("b1");
        NetworkNode b2 = new NetworkNode("b2");
        NetworkNode b3 = new NetworkNode("b3");
        NetworkNode b4 = new NetworkNode("b4");
        NetworkNode b5 = new NetworkNode("b5");

        NetworkEdge b12 = new NetworkEdge(b1, b2, 100);
        NetworkEdge b23 = new NetworkEdge(b2, b3, 10);
        NetworkEdge b24 = new NetworkEdge(b2, b4, 10);
        NetworkEdge b45 = new NetworkEdge(b4, b5, 1);

        graph.add(b1);
        graph.add(b2);
        graph.add(b3);
        graph.add(b4);
        graph.add(b5);

        graph.add(b12);
        graph.add(b23);
        graph.add(b24);
        graph.add(b45);

        System.out.println(graph);

        Set<NetworkEdge> edges = graph.getEdges(b2);

        assertThat(edges.size(), is(3));
        assertThat(edges, hasItem(b12));
        assertThat(edges, hasItem(b23));
        assertThat(edges, hasItem(b24));

        Set<NetworkNode> neighbours = graph.getNeighbours(b2);
        System.out.println(neighbours);
        assertThat(neighbours.size(), is(3));
        assertThat(neighbours, hasItem(b1));
        assertThat(neighbours, hasItem(b3));
        assertThat(neighbours, hasItem(b4));

    }
}