package at.ac.tuwien.dsg.emma.manager.overlay;

/**
 * CompleteGraph.
 */
public class CompleteGraph extends AbstractUndirectedGraph {

    @Override
    public void add(Node node) {
        if (contains(node)) {
            return;
        }

        for (Node existing : nodes) {
            UndirectedEdge edge = new UndirectedEdge(node, existing);
            edges.add(edge);
        }

        nodes.add(node);
    }

    @Override
    public void remove(Node node) {
        if (!nodes.remove(node)) {
            return;
        }

        edges.removeIf(e -> e.contains(node));
    }

    @Override
    public Edge connect(Node nodeU, Node nodeV) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Edge edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Edge edge) {
        throw new UnsupportedOperationException();
    }
}
