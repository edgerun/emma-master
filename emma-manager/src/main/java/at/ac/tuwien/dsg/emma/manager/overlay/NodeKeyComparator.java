package at.ac.tuwien.dsg.emma.manager.overlay;

import java.util.Comparator;

/**
 * NodeKeyComparator.
 */
public final class NodeKeyComparator implements Comparator<Node> {

    private static final NodeKeyComparator INSTANCE = new NodeKeyComparator();

    @Override
    public int compare(Node o1, Node o2) {
        return o1.getId().compareTo(o2.getId());
    }

    public static NodeKeyComparator getInstance() {
        return INSTANCE;
    }
}
