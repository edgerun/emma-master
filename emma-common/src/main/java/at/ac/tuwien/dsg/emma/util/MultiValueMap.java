package at.ac.tuwien.dsg.emma.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * MultiValueMap.
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

    boolean addValue(K key, V value);

    boolean removeValue(K key, V value);

    /**
     * Returns the total amount of values over all collections.
     *
     * @return a long
     */
    long flatSize();

    /**
     * Executes the given action for every value in this collection.
     *
     * @param action the action
     */
    default void flatForEach(BiConsumer<? super K, ? super V> action) {
        for (Entry<K, List<V>> entry : entrySet()) {
            K key = entry.getKey();
            for (V value : entry.getValue()) {
                action.accept(key, value);
            }
        }
    }

    /**
     * Returns the value for this key, maybe.
     *
     * @param key the key
     * @return an optional
     */
    default Optional<List<V>> maybe(Object key) {
        return Optional.ofNullable(get(key));
    }
}
