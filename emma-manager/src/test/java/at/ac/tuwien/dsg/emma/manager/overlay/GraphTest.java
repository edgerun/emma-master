package at.ac.tuwien.dsg.emma.manager.overlay;

import org.junit.Test;

/**
 * GraphTest.
 */
public class GraphTest {
    @Test
    public void print() throws Exception {
        Node n1 = new Node("foo", NodeType.BROKER);
        Node n2 = new Node("bar", NodeType.GATEWAY);

        UndirectedEdge edge = new UndirectedEdge(n1, n2);

        System.out.println(edge);
        System.out.println(n1);
        System.out.println(n2);
    }

}