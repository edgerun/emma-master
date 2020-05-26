package io.edgerun.emma.controller.network.graph;

import java.util.Collection;
import java.util.Optional;

/**
 * Graph.
 */
public interface Graph<V, E> {

    Collection<Node<V>> getNodes();

    Collection<Edge<V, E>> getEdges();

    Collection<Edge<V, E>> getEdges(Node node);

    boolean addNode(Node node);

    Edge<V, E> addEdge(Node u, Node v);

    Node<V> getNode(String nodeId);

    Edge<V, E> getEdge(Node u, Node v);

    boolean removeEdge(Edge edge);

    boolean removeNode(Node node);

    Collection<Node<V>> getNeighbours(Node node);

    Optional<Node<V>> findNode(String nodeId);

    Optional<Edge<V, E>> findEdge(Node u, Node v);

    boolean isConnected(Node u, Node v);

    int degree(Node node);

}
