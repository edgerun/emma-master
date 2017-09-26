package at.ac.tuwien.dsg.emma.controller.event;

import at.ac.tuwien.dsg.emma.controller.model.Host;

/**
 * LatencyUpdateEvent.
 */
public class LatencyUpdateEvent implements SystemEvent {

    private Host source;
    private Host target;
    private long latency;

    public LatencyUpdateEvent(Host source, Host target, long latency) {
        this.source = source;
        this.target = target;
        this.latency = latency;
    }

    public Host getSource() {
        return source;
    }

    public Host getTarget() {
        return target;
    }

    public long getLatency() {
        return latency;
    }

    @Override
    public String toString() {
        return "LatencyUpdateEvent{" +
                "source=" + source +
                ", target=" + target +
                ", latency=" + latency +
                '}';
    }
}
