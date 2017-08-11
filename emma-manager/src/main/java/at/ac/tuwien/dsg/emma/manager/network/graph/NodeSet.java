package at.ac.tuwien.dsg.emma.manager.network.graph;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

/**
 * NodeSet.
 */
public class NodeSet extends HashSet<Node> {

    private static final long serialVersionUID = 1L;

    public Optional<Node> find(String nodeId) {
        return stream().filter(node -> Objects.equals(nodeId, node.getId())).findFirst();
    }
}
