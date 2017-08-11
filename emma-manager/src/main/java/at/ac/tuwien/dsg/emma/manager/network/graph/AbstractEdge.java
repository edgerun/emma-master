package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Objects;

/**
 * AbstractEdge.
 */
public abstract class AbstractEdge<V> implements Edge<V> {

    protected V value;

    @Override
    public boolean contains(Node node) {
        return Objects.equals(node, getNodeU()) || Objects.equals(node, getNodeV());
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
    public V getValue() {
        return value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (isDirected()) {
            return "(" + getNodeU() + ")-->(" + getNodeV() + "){" + value + "}";
        } else {
            return "(" + getNodeU() + ")<->(" + getNodeV() + "){" + value + "}";
        }
    }
}
