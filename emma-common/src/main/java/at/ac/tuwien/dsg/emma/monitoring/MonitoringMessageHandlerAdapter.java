package at.ac.tuwien.dsg.emma.monitoring;

import at.ac.tuwien.dsg.emma.monitoring.msg.PingMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingReqMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PingRespMessage;
import at.ac.tuwien.dsg.emma.monitoring.msg.PongMessage;

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
}
