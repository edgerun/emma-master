package at.ac.tuwien.dsg.emma.bridge;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * BridgingTableEntry.
 */
public class BridgingTableEntry {

    private String topic;
    private String source;
    private String destination;

    public BridgingTableEntry() {

    }

    public BridgingTableEntry(String topic, InetSocketAddress source, InetSocketAddress destination) {
        this.topic = topic;
        this.source = source.getHostString() + ":" + source.getPort();
        this.destination = destination.toString() + ":" + source.getPort();
    }

    public BridgingTableEntry(String topic, String source, String destination) {
        this.topic = topic;
        this.source = source;
        this.destination = destination;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BridgingTableEntry that = (BridgingTableEntry) o;
        return Objects.equals(topic, that.topic) &&
                Objects.equals(source, that.source) &&
                Objects.equals(destination, that.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topic, source, destination);
    }

    @Override
    public String toString() {
        return "BridgingTableEntry{" +
                "topic='" + topic + '\'' +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
