package at.ac.tuwien.dsg.emma.overlay.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Route.
 */
public class Route {

    private List<NetworkEdge> edges;

    public Route() {
        this(new ArrayList<>());
    }

    public Route(List<NetworkEdge> edges) {
        this.edges = edges;
    }

    public void add(NetworkEdge edge) {
        edges.add(edge);
    }

    public List<NetworkEdge> getEdges() {
        return edges;
    }
}
