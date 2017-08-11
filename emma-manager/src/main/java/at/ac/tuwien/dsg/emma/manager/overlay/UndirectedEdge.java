package at.ac.tuwien.dsg.emma.manager.overlay;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * UndirectedEdge.
 */
public class UndirectedEdge implements Edge {

    private final AscendingPair<Node> nodes;
    private final Map<String, Object> metrics;

    public UndirectedEdge(Node a, Node b) {
        this.nodes = AscendingPair.of(a, b, NodeKeyComparator.getInstance());
        this.metrics = new HashMap<>();
    }

    public Pair<Node> getNodes() {
        return nodes;
    }

    @Override
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    @Override
    public Node opposite(Node node) {
        if (Objects.equals(node, getNodeU())) {
            return getNodeV();
        } else if (Objects.equals(node, getNodeV())) {
            return getNodeU();
        } else {
            return null;
        }
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

    @Override
    public Map<String, Object> getMetrics() {
        return metrics;
    }

    @Override
    public String toString() {
        return "(" + nodes.getFirst() + ")---(" + nodes.getSecond() + ")";
    }
}
