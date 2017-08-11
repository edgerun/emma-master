package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.Objects;

/**
 * Node.
 */
public class Node<V> {

    private final String id;

    private V value;

    public Node(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
