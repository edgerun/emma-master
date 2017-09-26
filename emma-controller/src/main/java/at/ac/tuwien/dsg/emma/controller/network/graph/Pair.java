package at.ac.tuwien.dsg.emma.controller.network.graph;

/**
 * Pair.
 */
public interface Pair<V> {
    V getFirst();

    V getSecond();

    boolean contains(V value);
}
