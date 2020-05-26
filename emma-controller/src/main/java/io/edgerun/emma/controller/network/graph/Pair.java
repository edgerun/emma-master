package io.edgerun.emma.controller.network.graph;

/**
 * Pair.
 */
public interface Pair<V> {
    V getFirst();

    V getSecond();

    boolean contains(V value);
}
