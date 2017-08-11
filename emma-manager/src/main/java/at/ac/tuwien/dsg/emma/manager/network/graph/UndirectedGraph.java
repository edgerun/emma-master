package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * UndirectedGraph.
 */
public class UndirectedGraph extends AbstractGraph {

    private Map<AscendingPair<Node>, Edge> edges;

    public UndirectedGraph() {
        this.edges = new HashMap<>();
    }

    @Override
    public Collection<Edge> getEdges() {
        return edges.values();
    }

    @Override
    public Edge addEdge(Node u, Node v) {
        return edges.computeIfAbsent(pair(u, v), EdgeImpl::new);
    }

    @Override
    public Edge getEdge(Node u, Node v) {
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
    public Collection<Node> getNeighbours(Node node) {
        Set<Node> neighbours = new HashSet<>();

        for (Edge edge : getEdges()) {
            Node other = edge.opposite(node);
            if (other != null) {
                neighbours.add(other);
            }
        }

        return neighbours;
    }

    private AscendingPair<Node> pair(Node u, Node v) {
        return AscendingPair.of(u, v, NodeKeyComparator.getInstance());
    }

    /**
     * UndirectedEdge.
     */
    public static class EdgeImpl<V> extends AbstractEdge<V> {

        private final AscendingPair<Node> nodes;

        public EdgeImpl(AscendingPair<Node> nodes) {
            this.nodes = nodes;
        }

        public Pair<Node> getNodes() {
            return nodes;
        }

        @Override
        public Node getNodeU() {
            return nodes.getFirst();
        }

        @Override
        public Node getNodeV() {
            return nodes.getSecond();
        }

        @Override
        public boolean isDirected() {
            return false;
        }

    }
}
