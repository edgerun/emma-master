package at.ac.tuwien.dsg.emma.overlay.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * NetworkGraph.
 */
public class NetworkGraph {

    private Set<NetworkNode> vertices;
    private Set<NetworkEdge> edges;

    public NetworkGraph() {
        this.vertices = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public void add(NetworkNode vertex) {
        vertices.add(vertex);
    }

    public void add(NetworkEdge edge) {
        edges.add(edge);
    }

    public NetworkEdge add(NetworkNode v1, NetworkNode v2, int weight) {
        vertices.add(v1);
        vertices.add(v2);

        NetworkEdge e = new NetworkEdge(v1, v2);
        e.setWeight(weight);
        edges.add(e);
        return e;
    }

    public Set<NetworkEdge> getEdges(NetworkNode v) {
        Predicate<NetworkEdge> isLeft = e -> v.equals(e.getLeft());
        Predicate<NetworkEdge> isRight = e -> v.equals(e.getRight());

        return edges.stream()
                .filter(isLeft.or(isRight))
                .collect(Collectors.toSet());
    }

    public Set<NetworkNode> getNeighbours(NetworkNode v) {
        return getEdges(v).stream()
                .map(e -> v.equals(e.getRight()) ? e.getLeft() : e.getRight())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Graph {").append("\n");

        for (NetworkEdge edge : edges) {
            str.append("  ").append(edge).append("\n");
        }

        str.append("}");

        return str.toString();
    }
}

