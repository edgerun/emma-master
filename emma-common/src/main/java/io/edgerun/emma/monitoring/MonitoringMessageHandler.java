package io.edgerun.emma.monitoring;

import io.edgerun.emma.monitoring.msg.PingMessage;
import io.edgerun.emma.monitoring.msg.PingReqMessage;
import io.edgerun.emma.monitoring.msg.PingRespMessage;
import io.edgerun.emma.monitoring.msg.PongMessage;
import io.edgerun.emma.monitoring.msg.ReconnectAck;
import io.edgerun.emma.monitoring.msg.ReconnectRequest;
import io.edgerun.emma.monitoring.msg.UsageRequest;
import io.edgerun.emma.monitoring.msg.UsageResponse;

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
