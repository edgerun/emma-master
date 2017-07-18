package at.ac.tuwien.dsg.emma.bridge;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.xenqtt.client.MqttClientListener;
import net.xenqtt.client.SyncMqttClient;

/**
 * XenqttBridgeConnection.
 */
public class XenqttBridgeConnection implements Closeable {

    private final String uri;
    private final SyncMqttClient client;
    private final Set<String> subscriptions;

    public XenqttBridgeConnection(String uri, MqttClientListener listener) {
        this.uri = uri;
        this.client = new SyncMqttClient(uri, listener, 1);
        this.subscriptions = new HashSet<>();
    }

    public String getUri() {
        return uri;
    }

    public SyncMqttClient getClient() {
        return client;
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    @Override
    public void close() throws IOException {
        client.disconnect();
        client.close();
    }
}
