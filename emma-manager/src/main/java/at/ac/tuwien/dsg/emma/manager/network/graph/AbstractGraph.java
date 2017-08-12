package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

/**
 * AbstractGraph.
 */
public abstract class AbstractGraph implements Graph {

    protected HashMap<String, Node> nodes;

    public AbstractGraph() {
        this.nodes = new HashMap<>();
    }

    @Override
    public Collection<Node> getNodes() {
        return nodes.values();
    }

    @Override
    public boolean addNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            return false;
        }

        nodes.put(node.getId(), node);
        return true;
    }

    @Override
    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    @Override
    public boolean removeNode(Node node) {
        boolean removed = nodes.remove(node.getId()) != null;

        if (removed) {
            getEdges().removeIf(e -> e.contains(node));
        }

        return removed;
    }

    @Override
    public Optional<Node> findNode(String nodeId) {
        return Optional.ofNullable(getNode(nodeId));
    }

    @Override
    public Optional<Edge> findEdge(Node u, Node v) {
        return Optional.ofNullable(getEdge(u, v));
    }

    @Override
    public boolean isConnected(Node u, Node v) {
        return findEdge(u, v).isPresent();
    }

    @Override
    public int degree(Node node) {
        return getEdges(node).size();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getClass().getSimpleName());
        str.append("{");

        Iterator<Edge> iterator = getEdges().iterator();

        if (iterator.hasNext()) {
            str.append(System.lineSeparator());
        }

        while (iterator.hasNext()) {
            Edge next = iterator.next();
            str.append("  ").append(next).append(System.lineSeparator());
        }

        str.append("}");
        return str.toString();
    }
}
