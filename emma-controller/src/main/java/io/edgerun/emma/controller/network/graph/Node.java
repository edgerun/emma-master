package io.edgerun.emma.controller.network.graph;

import java.util.Objects;

/**
 * Node.
 */
public class Node<V> {

    private final String id;

    private V value;

    public Node(String id) {
        this(id, null);
    }

    public Node(String id, V value) {
        this.id = Objects.requireNonNull(id);
        this.value = value;
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

    @Override
    public String toString() {
        return '(' + id + "){" + value + '}';
    }
}
