package at.ac.tuwien.dsg.emma.ec;

import java.net.SocketAddress;
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

    private SocketAddress requestor;
    private SocketAddress source;
    private SocketAddress target;

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

    public SocketAddress getRequestor() {
        return requestor;
    }

    public void setRequestor(SocketAddress requestor) {
        this.requestor = requestor;
    }

    public SocketAddress getSource() {
        return source;
    }

    public void setSource(SocketAddress source) {
        this.source = source;
    }

    public SocketAddress getTarget() {
        return target;
    }

    public void setTarget(SocketAddress target) {
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
