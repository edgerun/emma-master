package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Collection;
import java.util.Optional;

/**
 * Graph.
 */
public interface Graph {

    Collection<Node> getNodes();

    Collection<Edge> getEdges();

    Collection<Edge> getEdges(Node node);

    boolean addNode(Node node);

    <V> Edge<V> addEdge(Node u, Node v);

    <V> Node<V> getNode(String nodeId);

    <V> Edge<V> getEdge(Node u, Node v);

    boolean removeEdge(Edge edge);

    boolean removeNode(Node node);

    Collection<Node> getNeighbours(Node node);

    Optional<Node> findNode(String nodeId);

    Optional<Edge> findEdge(Node u, Node v);

    boolean isConnected(Node u, Node v);

    int degree(Node node);

}
