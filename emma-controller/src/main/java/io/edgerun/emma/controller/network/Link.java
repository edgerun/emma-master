package io.edgerun.emma.controller.network;

import io.edgerun.emma.util.LongWindow;

/**
 * Link.
 */
public class Link {

    private LongWindow latency;

    private boolean connected;

    public Link() {
        this(new LongWindow(4));
    }

    public Link(LongWindow latency) {
        this.latency = latency;
    }

    public LongWindow getLatency() {
        return latency;
    }

    public void setLatency(LongWindow latency) {
        this.latency = latency;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /*
     * used for tests
     */
    public Link addLatency(long latency) {
        this.latency.add(latency);
        return this;
    }

    @Override
    public String toString() {
        return "Link{" +
                "latency=" + latency +
                '}';
    }
}
