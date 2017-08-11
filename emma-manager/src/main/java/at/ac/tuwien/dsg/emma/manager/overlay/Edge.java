package at.ac.tuwien.dsg.emma.manager.overlay;

import java.util.Map;

/**
 * Edge.
 */
public interface Edge {

    Node getNodeU();

    Node getNodeV();

    boolean isDirected();

    boolean contains(Node node);

    Node opposite(Node node);

    Map<String, Object> getMetrics();
}
