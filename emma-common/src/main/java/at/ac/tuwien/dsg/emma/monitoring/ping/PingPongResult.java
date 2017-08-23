package at.ac.tuwien.dsg.emma.monitoring.ping;

import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LongSummaryStatistics;

import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;

/**
 * PingPongResult.
 */
public class PingPongResult {

    private PingReqMessage request;
    private Collection<PingPong> pingPongs;

    public PingPongResult(PingReqMessage request, Collection<PingPong> pingPongs) {
        this.request = request;
        this.pingPongs = pingPongs;
    }

    public PingReqMessage getRequest() {
        return request;
    }

    public int getLostPacketCount() {
        return (int) pingPongs.stream().filter(pp -> pp.getReceived() == null).count();
    }

    public LongSummaryStatistics getStatistics() {
        return pingPongs.stream()
                .filter(pp -> pp.getReceived() != null)
                .map(pp -> pp.getSent().until(pp.getReceived(), ChronoUnit.MILLIS))
                .mapToLong(i -> i)
                .summaryStatistics();
    }

}
