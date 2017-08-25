package at.ac.tuwien.dsg.emma.monitoring;

import at.ac.tuwien.dsg.emma.monitoring.msg.PingMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.ReconnectAck;
import at.ac.tuwien.dsg.emma.monitoring.msg.ReconnectRequest;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageRequest;
import at.ac.tuwien.dsg.emma.monitoring.msg.UsageResponse;

/**
 * MonitoringMessageHandler.
 */
public interface MonitoringMessageHandler {
    void onMessage(MonitoringLoop loop, PingMessage message);

    void onMessage(MonitoringLoop loop, PongMessage message);

    void onMessage(MonitoringLoop loop, PingReqMessage message);

    void onMessage(MonitoringLoop loop, PingRespMessage message);

    void onMessage(MonitoringLoop loop, ReconnectRequest message);
    void onMessage(MonitoringLoop loop, ReconnectAck message);

    void onMessage(MonitoringLoop loop, UsageRequest message);

    void onMessage(MonitoringLoop loop, UsageResponse message);


}
