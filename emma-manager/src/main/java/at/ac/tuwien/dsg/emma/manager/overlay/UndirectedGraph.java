package at.ac.tuwien.dsg.emma.manager.overlay;

/**
 * UndirectedGraph.
 */
public interface UndirectedGraph {

    EdgeSet getEdges();

    NodeSet getNodes();

    void add(Node node);

    void add(Edge edge);

    void remove(Edge edge);

    void remove(Node node);

    Edge connect(Node nodeU, Node nodeV);

    NodeSet getNeighbours(Node node);

    EdgeSet getEdges(Node node);

    boolean contains(Node node);

    boolean contains(Edge edge);

}
