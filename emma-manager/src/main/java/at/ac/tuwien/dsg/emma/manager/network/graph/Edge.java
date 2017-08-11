package at.ac.tuwien.dsg.emma.manager.network.graph;

/**
 * Edge.
 */
public interface Edge<V> {

    Node getNodeU();

    Node getNodeV();

    boolean isDirected();

    boolean contains(Node node);

    Node opposite(Node node);

    V getValue();

    void setValue(V value);
}
