package io.edgerun.emma.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * MultiValueHashMap.
 */
public class MultiValueHashMap<K, V> extends HashMap<K, List<V>> implements MultiValueMap<K, V> {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean addValue(K key, V value) {
        return computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    @Override
    public boolean removeValue(K key, V value) {
        return maybe(key).map(l -> l.remove(value)).orElse(false);
    }

    @Override
    public long flatSize() {
        return values().stream().map(List::size).mapToLong(i -> i).sum();
    }

}
