package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * UndirectedGraph.
 */
public class UndirectedGraph<V, E> extends AbstractGraph<V, E> {

    private Map<AscendingPair<Node<V>>, Edge<V, E>> edges;

    public UndirectedGraph() {
        this.edges = new ConcurrentHashMap<>();
    }

    @Override
    public Collection<Edge<V, E>> getEdges() {
        return edges.values();
    }

    @Override
    public Collection<Edge<V, E>> getEdges(Node node) {
        return edges.values().stream()
                .filter(e -> e.contains(node))
                .collect(Collectors.toSet());
    }

    @Override
    public Edge<V, E> addEdge(Node u, Node v) {
        return edges.computeIfAbsent(pair(u, v), EdgeImpl::new);
    }

    @Override
    public Edge<V, E> getEdge(Node u, Node v) {
        return edges.get(pair(u, v));
    }

    @Override
    public boolean removeEdge(Edge edge) {
        if (!(edge instanceof EdgeImpl)) {
            return false;
        }

        EdgeImpl e = ((EdgeImpl) edge);

        return edges.remove(e.nodes) != null;
    }

    @Override
    public Collection<Node<V>> getNeighbours(Node node) {
        Set<Node<V>> neighbours = new HashSet<>();

        for (Edge<V, E> edge : getEdges()) {
            Node<V> other = edge.opposite(node);
            if (other != null) {
                neighbours.add(other);
            }
        }

        return neighbours;
    }

    private AscendingPair<Node<V>> pair(Node<V> u, Node<V> v) {
        return AscendingPair.of(u, v, NodeKeyComparator.getInstance());
    }

    /**
     * UndirectedEdge.
     */
    static class EdgeImpl<V, E> extends AbstractEdge<V, E> {

        private final AscendingPair<Node<V>> nodes;

        public EdgeImpl(AscendingPair<Node<V>> nodes) {
            this.nodes = nodes;
        }

        public Pair<Node<V>> getNodes() {
            return nodes;
        }

        @Override
        public Node<V> getNodeU() {
            return nodes.getFirst();
        }

        @Override
        public Node<V> getNodeV() {
            return nodes.getSecond();
        }

        @Override
        public boolean isDirected() {
            return false;
        }

    }
}
