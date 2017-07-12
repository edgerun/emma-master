package at.ac.tuwien.dsg.emma.overlay.graph;

import java.util.Objects;

/**
 * NetworkEdge.
 */
public class NetworkEdge {

    private final NetworkNode left;
    private final NetworkNode right;

    private int weight;

    public NetworkEdge(NetworkNode left, NetworkNode right) {
        this.left = left;
        this.right = right;
    }

    public NetworkEdge(NetworkNode left, NetworkNode right, int weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
    }

    public NetworkNode getLeft() {
        return left;
    }

    public NetworkNode getRight() {
        return right;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NetworkEdge that = (NetworkEdge) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return String.format("%s<--[%3s]-->%s", left, weight, right);
    }
}
