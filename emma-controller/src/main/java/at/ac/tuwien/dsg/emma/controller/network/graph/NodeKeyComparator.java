package at.ac.tuwien.dsg.emma.controller.network.graph;

import java.util.Comparator;

/**
 * NodeKeyComparator.
 */
public final class NodeKeyComparator<V> implements Comparator<Node<V>> {

    private static final NodeKeyComparator INSTANCE = new NodeKeyComparator();

    @Override
    public int compare(Node<V> o1, Node<V> o2) {
        return o1.getId().compareTo(o2.getId());
    }

    public static <V> NodeKeyComparator<V> getInstance() {
        return INSTANCE;
    }
}
