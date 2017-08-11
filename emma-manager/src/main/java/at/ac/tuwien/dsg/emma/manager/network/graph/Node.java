package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Node.
 */
public class Node {

    private final String id;
    private final NodeType type;
    private final Map<String, Double> metrics;

    public Node(String id, NodeType type) {
        this.id = Objects.requireNonNull(id);
        this.type = Objects.requireNonNull(type);
        this.metrics = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public NodeType getType() {
        return type;
    }

    public Map<String, Double> getMetrics() {
        return metrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return type + "{" + id + "}";
    }
}
