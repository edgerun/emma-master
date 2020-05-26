package io.edgerun.emma.controller.network.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * GraphTest.
 */
public abstract class GraphTest {

    @Test
    public void findNode_returnsCorrectOptional() throws Exception {
        Graph graph = createGraph();
        Node node = new Node("foo");

        graph.addNode(node);

        assertEquals(node, graph.findNode("foo").get());
        assertFalse(graph.findNode("bar").isPresent());
    }

    @Test
    public void removeNode_removesNode() throws Exception {
        Graph graph = createGraph();
        Node n1 = new Node("foo");
        Node n2 = new Node("bar");

        graph.addNode(n1);
        graph.addNode(n2);

        graph.removeNode(graph.getNode("foo"));

        assertNull(graph.getNode("foo"));
        assertEquals(1, graph.getNodes().size());
    }

    @Test
    public void removeNode_removesEdges() throws Exception {
        Graph graph = createGraph();
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");
        Node n3 = new Node("n3");

        graph.addNode(n1);
        graph.addNode(n2);
        graph.addNode(n3);

        graph.addEdge(n1, n2); // gets removed
        graph.addEdge(n1, n3); // gets removed
        graph.addEdge(n2, n3);

        graph.removeNode(graph.getNode("n1"));

        assertEquals(1, graph.getEdges().size());

        assertNotNull(graph.getEdge(n2, n3));
    }

    @Test
    public void getEdge_returnsCorrectEdge() throws Exception {
        Graph graph = createGraph();
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        graph.addNode(n1);
        graph.addNode(n2);

        Edge e = graph.addEdge(n1, n2);

        assertEquals(e, graph.getEdge(n1, n2));
    }

    @Test
    public void getNodes_returnsNodeCollection() throws Exception {
        Graph graph = createGraph();

        assertTrue(graph.getNodes().isEmpty());

        graph.addNode(new Node("foo"));
        assertFalse(graph.getNodes().isEmpty());

        Node found = (Node) graph.getNodes().iterator().next();

        assertEquals("foo", found.getId());
    }

    @Test
    public void addNode_onExistingNode_returnsFalse() throws Exception {
        Graph graph = createGraph();

        Node node = new Node("foo");
        assertTrue(graph.addNode(node));
        assertFalse(graph.addNode(node));
        assertFalse(graph.addNode(new Node("foo")));
    }

    @Test
    public void addEdge_onExistingEdge_doesNotCreateNewEdge() throws Exception {
        Graph graph = createGraph();
        Node n1 = new Node("n1");
        Node n2 = new Node("n2");

        graph.addNode(n1);
        graph.addNode(n2);

        Edge edge = graph.addEdge(n1, n2);
        assertEquals(1, graph.getEdges().size());

        assertEquals(edge, graph.addEdge(n1, n2));
        assertEquals(1, graph.getEdges().size());
    }

    abstract Graph createGraph();
}