package at.ac.tuwien.dsg.emma.manager.network;

/**
 * Link.
 */
public class Link {

    private Double latency;

    // TODO: hops?

    public Double getLatency() {
        return latency;
    }

    public Link setLatency(double latency) {
        this.latency = latency;
        return this;
    }

    @Override
    public String toString() {
        return "Link{" +
                "latency=" + latency +
                '}';
    }
}
