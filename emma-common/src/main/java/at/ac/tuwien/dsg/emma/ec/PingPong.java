package at.ac.tuwien.dsg.emma.ec;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * PingPong.
 */
public class PingPong {

    private int id;

    private Instant requested;
    private Instant sent;
    private Instant received;

    private InetSocketAddress requestor;
    private InetSocketAddress source;
    private InetSocketAddress target;

    public PingPong(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Instant getRequested() {
        return requested;
    }

    public void setRequested(Instant requested) {
        this.requested = requested;
    }

    public Instant getSent() {
        return sent;
    }

    public void setSent(Instant sent) {
        this.sent = sent;
    }

    public Instant getReceived() {
        return received;
    }

    public void setReceived(Instant received) {
        this.received = received;
    }

    public InetSocketAddress getRequestor() {
        return requestor;
    }

    public void setRequestor(InetSocketAddress requestor) {
        this.requestor = requestor;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public void setSource(InetSocketAddress source) {
        this.source = source;
    }

    public InetSocketAddress getTarget() {
        return target;
    }

    public void setTarget(InetSocketAddress target) {
        this.target = target;
    }

    public long calculateLatency() {
        if (sent != null && received != null) {
            return sent.until(received, ChronoUnit.MILLIS);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return "PingPong{" +
                "id=" + id +
                ", requested=" + requested +
                ", sent=" + sent +
                ", received=" + received +
                ", requestor=" + requestor +
                ", source=" + source +
                ", target=" + target +
                '}';
    }
}
