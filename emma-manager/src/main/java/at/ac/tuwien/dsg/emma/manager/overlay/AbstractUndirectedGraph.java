package at.ac.tuwien.dsg.emma.manager.overlay;

public abstract class AbstractUndirectedGraph implements UndirectedGraph {

    protected NodeSet nodes;
    protected EdgeSet edges;

    public AbstractUndirectedGraph() {
        this.nodes = new NodeSet();
        this.edges = new EdgeSet();
    }

    @Override
    public EdgeSet getEdges() {
        return edges;
    }

    @Override
    public NodeSet getNodes() {
        return nodes;
    }

    @Override
    public NodeSet getNeighbours(Node node) {
        NodeSet neighbours = new NodeSet();

        for (Edge edge : edges) {
            Node opposite = edge.opposite(node);
            if (opposite != null) {
                neighbours.add(opposite);
            }
        }

        return neighbours;
    }

    @Override
    public EdgeSet getEdges(Node node) {
        EdgeSet connections = new EdgeSet();

        for (Edge edge : edges) {
            if (edge.contains(node)) {
                connections.add(edge);
            }
        }

        return connections;
    }

    @Override
    public boolean contains(Node node) {
        return nodes.contains(node);
    }

    @Override
    public boolean contains(Edge edge) {
        return edges.contains(edge);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getClass().getSimpleName());
        str.append("{").append(System.lineSeparator());

        for (Edge edge : edges) {
            str.append("  ").append(edge).append(edge.getMetrics()).append(System.lineSeparator());
        }

        str.append("}");
        return str.toString();
    }
}
