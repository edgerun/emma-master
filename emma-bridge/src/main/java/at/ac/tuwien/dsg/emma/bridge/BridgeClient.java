package at.ac.tuwien.dsg.emma.bridge;

import static at.ac.tuwien.dsg.emma.bridge.util.PublishMessageAccessor.getPubMessage;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

import net.xenqtt.client.MqttClient;
import net.xenqtt.client.MqttClientListener;
import net.xenqtt.client.PublishMessage;
import net.xenqtt.client.Subscription;
import net.xenqtt.client.SyncMqttClient;
import net.xenqtt.message.QoS;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * BridgeClient.
 */
public class BridgeClient implements Runnable, Closeable {

    private SyncMqttClient local;
    private SyncMqttClient remote;

    private String clientId;
    private String localUri;
    private String remoteUri;

    public BridgeClient(String clientId, String localUri, String remoteUri) {
        this.clientId = clientId;
        this.localUri = localUri;
        this.remoteUri = remoteUri;
    }

    @Override
    public void run() {
        local = new SyncMqttClient(localUri, new LocalListener(), 1);
        remote = new SyncMqttClient(remoteUri, new RemoteListener(), 1);

        System.out.println("Connecting to local broker");
        local.connect(clientId + "-local", true);
        System.out.println("Connecting to remote broker");
        remote.connect(clientId + "-remote", true);

        System.out.println("Subscribing to local topics");
        local.subscribe(new Subscription[]{new Subscription("testTopic", QoS.AT_LEAST_ONCE)});
        //        local.subscribe(new Subscription[]{new Subscription("$fwd/testTopic", QoS.AT_LEAST_ONCE)});

        System.out.println("Subscribing to remote topic");
        //        remote.subscribe(new Subscription[]{new Subscription("testTopic", QoS.AT_LEAST_ONCE)});
        remote.subscribe(new Subscription[]{new Subscription("$fwd/testTopic", QoS.AT_LEAST_ONCE)});

        System.out.println(clientId + " bridgin " + localUri + " <--->> " + remoteUri);
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(() -> {
            local.disconnect();
            local.close();
        });
        IOUtils.close(() -> {
            remote.disconnect();
            remote.close();
        });
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
        }

        private boolean isEcho(PublishMessage message) {
            // FIXME this will not work if client uses QoS > 0!
            return getPubMessage(message).getMessageId() != 0;
        }

        @Override
        public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {

        }
    }

    private class RemoteListener implements MqttClientListener {

        @Override
        public void publishReceived(MqttClient client, PublishMessage message) {
            message.ack();

            System.out.println("    message id: " + getPubMessage(message).getMessageId());
            String topic = message.getTopic().substring(5); // remove "$fwd" prefix

            PublishMessage fwd = new PublishMessage(topic, QoS.AT_LEAST_ONCE, message.getPayload(), message.isRetain());
            local.publish(fwd);
        }

        @Override
        public void disconnected(MqttClient client, Throwable cause, boolean reconnecting) {

        }
    }

    /**
     * FIXME: this is a test to connect two brokers
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        BridgeClient bridgeClient1 = new BridgeClient("c1883", "tcp://localhost:1883", "tcp://localhost:1884");
        BridgeClient bridgeClient2 = new BridgeClient("c1884", "tcp://localhost:1884", "tcp://localhost:1883");

        Thread bridgeClient1Thread = new Thread(bridgeClient1);
        Thread bridgeClient2Thread = new Thread(bridgeClient2);

        bridgeClient1Thread.start();
        bridgeClient2Thread.start();

        System.out.println("Press enter to exit");
        new Scanner(System.in).nextLine();

        System.out.println("Closing BC1");
        bridgeClient1.close();
        System.out.println("Closing BC2");
        bridgeClient2.close();
        System.out.println("Waiting for BC1 thread to end");
        bridgeClient1Thread.join();
        System.out.println("Waiting for BC2 thread to end");
        bridgeClient2Thread.join();

        System.out.println("Done, bye!");
    }
}
