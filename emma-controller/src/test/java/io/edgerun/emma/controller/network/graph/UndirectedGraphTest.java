package io.edgerun.emma.controller.network.graph;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * UndirectedGraphTest.
 */
public class UndirectedGraphTest extends GraphTest {
    @Override
    UndirectedGraph createGraph() {
        return new UndirectedGraph();
    }

    @Test
    public void getEdge_returnsUndirectedEdge() throws Exception {
        UndirectedGraph graph = createGraph();

        Node foo = new Node("foo");
        Node bar = new Node("bar");

        graph.addNode(foo);
        graph.addNode(bar);

        Edge e = graph.addEdge(foo, bar);

        assertEquals(e, graph.getEdge(foo, bar));
        assertEquals(e, graph.getEdge(bar, foo));
    }

    @Test
    public void addEdge_onExistingEdge_doesNotCreateNewEdge() throws Exception {
        Graph graph = createGraph();
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        graph.addNode(n1);
        graph.addNode(n2);

        Edge e1 = graph.addEdge(n1, n2);
        Edge e2 = graph.addEdge(n2, n1);

        assertEquals(1, graph.getEdges().size());
        assertEquals(e1, e2);
    }

}
