package at.ac.tuwien.dsg.emma.monitoring;

import at.ac.tuwien.dsg.emma.monitoring.msg.PingMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;

/**
 * MonitoringMessageHandler.
 */
public interface MonitoringMessageHandler {
    void onMessage(MonitoringLoop loop, PingMessage message);

    void onMessage(MonitoringLoop loop, PongMessage message);

}
