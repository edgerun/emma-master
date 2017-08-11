package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Collection;
import java.util.Optional;

/**
 * Graph.
 */
public interface Graph {

    Collection<Node> getNodes();

    Collection<Edge> getEdges();

    boolean addNode(Node node);

    <V> Edge<V> addEdge(Node u, Node v);

    Node getNode(String nodeId);

    Edge getEdge(Node u, Node v);

    boolean removeEdge(Edge edge);

    boolean removeNode(Node node);

    Collection<Node> getNeighbours(Node node);

    Optional<Node> findNode(String nodeId);

    Optional<Edge> findEdge(Node u, Node v);

    boolean isConnected(Node u, Node v);

}
