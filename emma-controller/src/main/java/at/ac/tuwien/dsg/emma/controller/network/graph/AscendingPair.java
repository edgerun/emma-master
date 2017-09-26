package at.ac.tuwien.dsg.emma.controller.network.graph;

import java.util.Comparator;
import java.util.Objects;

/**
 * An AscendingPair orders its elements on creation s.t. {@link #getFirst()} <= {@link #getSecond()}, consequently
 * satisfying the condition (a,b) == (b,a).
 */
public class AscendingPair<V> implements Pair<V> {

    private final V first;
    private final V second;

    private AscendingPair(V a, V b, Comparator<V> comparator) {
        int c = comparator.compare(a, b);

        if (c < 0) {
            first = a;
            second = b;
        } else {
            first = b;
            second = a;
        }
    }

    public V getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public boolean contains(V value) {
        return Objects.equals(first, value) || Objects.equals(second, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AscendingPair<?> that = (AscendingPair<?>) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    public static <C extends Comparable<C>> AscendingPair<C> of(C a, C b) {
        return new AscendingPair<>(a, b, Comparable::compareTo);
    }

    public static <C> AscendingPair<C> of(C a, C b, Comparator<C> comparator) {
        return new AscendingPair<>(a, b, comparator);
    }

}
