package at.ac.tuwien.dsg.emma.monitoring.msg;

import at.ac.tuwien.dsg.emma.monitoring.MonitoringPacketType;

/**
 * ReconnectMessage.
 */
public class ReconnectMessage extends AbstractMonitoringMessage {

    @Override
    public MonitoringPacketType getMonitoringPacketType() {
        return MonitoringPacketType.RECONNECT;
    }
}
