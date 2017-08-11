package at.ac.tuwien.dsg.emma.manager.network.graph;

/**
 * Pair.
 */
public interface Pair<V> {
    V getFirst();

    V getSecond();

    boolean contains(V value);
}
