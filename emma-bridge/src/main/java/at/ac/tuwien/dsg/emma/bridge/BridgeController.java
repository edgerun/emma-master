package at.ac.tuwien.dsg.emma.bridge;

import java.util.concurrent.BlockingQueue;

/**
 * BridgeController.
 */
public class BridgeController implements Runnable {

    private final BridgeClient client;
    private final BlockingQueue<BridgingRequest> requests;

    public BridgeController(BridgeClient client, BlockingQueue<BridgingRequest> requests) {
        this.client = client;
        this.requests = requests;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BridgingRequest request = requests.take();

                if (request.isDisconnect()) {
                    client.disconnect(request.getBrokerUri(), request.getTopicId());
                } else {
                    client.connect(request.getBrokerUri(), request.getTopicId());
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public static class BridgingRequest {

        private final String brokerUri;
        private final String topicId;
        private final boolean disconnect;

        public BridgingRequest(String brokerUri, String topicId, boolean disconnect) {
            this.brokerUri = brokerUri;
            this.topicId = topicId;
            this.disconnect = disconnect;
        }

        public String getBrokerUri() {
            return brokerUri;
        }

        public String getTopicId() {
            return topicId;
        }

        public boolean isDisconnect() {
            return disconnect;
        }
    }
}
