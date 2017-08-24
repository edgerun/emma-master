package at.ac.tuwien.dsg.emma.monitoring.msg;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * UsageResponse.
 */
public class UsageResponse extends AbstractMonitoringMessage {

    private String hostId;
    private int processors;
    private float load;
    private int throughputIn;
    private int throughputOut;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public int getProcessors() {
        return processors;
    }

    public void setProcessors(int processors) {
        this.processors = processors;
    }

    public float getLoad() {
        return load;
    }

    public void setLoad(float load) {
        this.load = load;
    }

    public int getThroughputIn() {
        return throughputIn;
    }

    public void setThroughputIn(int throughputIn) {
        this.throughputIn = throughputIn;
    }

    public int getThroughputOut() {
        return throughputOut;
    }

    public void setThroughputOut(int throughputOut) {
        this.throughputOut = throughputOut;
    }

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.USAGERESP;
    }

    @Override
    public String toString() {
        return "UsageResponse{" +
                "hostId='" + hostId + '\'' +
                ", processors=" + processors +
                ", load=" + load +
                ", throughputIn=" + throughputIn +
                ", throughputOut=" + throughputOut +
                '}';
    }
}
