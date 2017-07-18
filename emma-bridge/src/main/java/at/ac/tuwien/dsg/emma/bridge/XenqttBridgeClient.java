package at.ac.tuwien.dsg.emma.bridge;

import static at.ac.tuwien.dsg.emma.bridge.util.PublishMessageAccessor.getPubMessage;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.xenqtt.client.MqttClient;
import net.xenqtt.client.MqttClientListener;
import net.xenqtt.client.PublishMessage;
import net.xenqtt.client.Subscription;
import net.xenqtt.client.SyncMqttClient;
import net.xenqtt.message.QoS;

import at.ac.tuwien.dsg.emma.util.IOUtils;

public class XenqttBridgeClient implements BridgeClient, Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(XenqttBridgeClient.class);

    private static final AtomicInteger remoteIdGenerator = new AtomicInteger(0);

    private String clientId;
    private String localUri;

    private SyncMqttClient local;

    private Map<String, XenqttBridgeConnection> bridges; // {remoteBrokerUri: client}
    private Set<String> localSubscriptions;

    private MqttClientListener localListener;
    private MqttClientListener remoteListener;

    public XenqttBridgeClient(String clientId, String localUri) {
        this.clientId = clientId;
        this.localUri = localUri;

        this.bridges = new HashMap<>();
        this.localSubscriptions = new HashSet<>();
        this.localListener = new LocalListener();
        this.remoteListener = new RemoteListener();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    public String getNextRemoteClientId() {
        return getClientId() + "-remote-" + remoteIdGenerator.incrementAndGet();
    }

    @Override
    public void run() {
        local = new SyncMqttClient(localUri, localListener, 1);
        local.connect(getClientId(), true);
    }

    @Override
    public synchronized void connect(String brokerUri, String topic) {
        XenqttBridgeConnection bridge = bridges.get(brokerUri);

        if (bridge == null) {
            LOG.info("{}: connecting to {}", getClientId(), brokerUri);
            bridge = connectBridge(brokerUri);
        }

        if (!bridge.getSubscriptions().contains(topic)) {
            LOG.info("{}: subscribing to {} on {}", getClientId(), "$fwd/" + topic, brokerUri);
            subscribe(topic, bridge);
        }

        if (!localSubscriptions.contains(topic)) {
            LOG.info("{}: subscribing to {} on local topic", getClientId(), topic);
            local.subscribe(new Subscription[]{new Subscription(topic, QoS.AT_LEAST_ONCE)});
            localSubscriptions.add(topic);
        }
    }

    private void subscribe(String topic, XenqttBridgeConnection bridge) {
        bridge.getClient().subscribe(new Subscription[]{new Subscription("$fwd/" + topic, QoS.AT_LEAST_ONCE)});
        bridge.getSubscriptions().add(topic);
    }

    private XenqttBridgeConnection connectBridge(String brokerUri) {
        XenqttBridgeConnection bridge = new XenqttBridgeConnection(brokerUri, remoteListener);
        bridge.getClient().connect(getNextRemoteClientId(), true);
        bridges.put(brokerUri, bridge);
        return bridge;
    }

    @Override
    public synchronized void disconnect(String broker, String topic) {
        XenqttBridgeConnection bridge = bridges.get(broker);
        if (bridge == null) {
            return;
        }
        if (!bridge.getSubscriptions().contains(topic)) {
            return;
        }

        bridge.getClient().unsubscribe(new String[]{"$fwd/" + topic});

        // TODO: "garbage collect" local subscriptions
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(() -> {
            local.disconnect();
            local.close();
        });

        bridges.forEach((k, c) -> IOUtils.close(c));
        bridges.clear();
    }

    private class LocalListener implements MqttClientListener {

        @Override
        public void publishReceived(MqttClient client, PublishMessage message) {
            if (isEcho(message)) {
                // Squelch echo
                message.ack();
                return;
            }

            String fwdTopic = "$fwd/" + message.getTopic();
            PublishMessage fwdMsg = new PublishMessage(fwdTopic, QoS.AT_LEAST_ONCE, message.getPayload(), message.isRetain());
            local.publish(fwdMsg);
            message.ack();
        }

        private boolean isEcho(PublishMessage message) {
            // FIXME this will not work if client uses QoS > 0!
            return getPubMessage(message).getMessageId() != 0;
        }

        @Override
        public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
            // TODO: disconnect cascade
        }
    }

    private class RemoteListener implements MqttClientListener {

        @Override
        public void publishReceived(MqttClient client, PublishMessage message) {
            message.ack();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Received remote message to distribute to local broker: {}", message);
            }

            String topic = message.getTopic().substring(5); // remove "$fwd" prefix
            PublishMessage fwd = new PublishMessage(topic, QoS.AT_LEAST_ONCE, message.getPayload(), message.isRetain());
            local.publish(fwd);
        }

        @Override
        public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {
            LOG.info("Client {} disconnecting, cause: {}", client, cause);
            // TODO: disconnect cascade
        }
    }

}
