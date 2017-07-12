package at.ac.tuwien.dsg.emma.overlay.graph;

import java.util.Objects;

/**
 * NetworkNode.
 */
public class NetworkNode {

    private final String id;

    public NetworkNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkNode that = (NetworkNode) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "(" + id + ")";
    }
}
