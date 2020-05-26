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
 * MonitoringMessageHandlerAdapter.
 */
public class MonitoringMessageHandlerAdapter implements MonitoringMessageHandler {

    @Override
    public void onMessage(MonitoringLoop loop, PingMessage message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, PongMessage message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, PingReqMessage message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, PingRespMessage message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, ReconnectRequest message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, ReconnectAck message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, UsageRequest message) {

    }

    @Override
    public void onMessage(MonitoringLoop loop, UsageResponse message) {

    }
}
