package at.ac.tuwien.dsg.emma.monitoring.msg;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * PingReqMessage.
 */
public class PingReqMessage extends AbstractMonitoringMessage {
    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.PINGREQ;
    }
}
