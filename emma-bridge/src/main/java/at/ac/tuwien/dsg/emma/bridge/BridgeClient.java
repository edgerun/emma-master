package at.ac.tuwien.dsg.emma.bridge;

/**
 * BridgeClient.
 */
public interface BridgeClient {

    String getClientId();

    void connect(String brokerUri, String topicName);

    void disconnect(String brokerUri, String topicName);
}
