package io.edgerun.emma.controller.network.graph;

/**
 * Edge.
 */
public interface Edge<V, E> {

    Node<V> getNodeU();

    Node<V> getNodeV();

    boolean isDirected();

    boolean contains(Node node);

    Node<V> opposite(Node node);

    E getValue();

    void setValue(E value);
}
