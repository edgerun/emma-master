package at.ac.tuwien.dsg.emma.monitoring;

import at.ac.tuwien.dsg.emma.monitoring.msg.PingMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;

/**
 * MonitoringMessageHandler.
 */
public interface MonitoringMessageHandler {
    void onMessage(MonitoringLoop loop, PingMessage message);

    void onMessage(MonitoringLoop loop, PongMessage message);

    void onMessage(MonitoringLoop loop, PingReqMessage message);

    void onMessage(MonitoringLoop loop, PingRespMessage message);

}
