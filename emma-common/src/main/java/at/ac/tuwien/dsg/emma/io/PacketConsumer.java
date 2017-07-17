package at.ac.tuwien.dsg.emma.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.emma.mqtt.MqttPacket;

/**
 * PacketConsumer.
 */
public class PacketConsumer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(PacketConsumer.class);

    private BlockingQueue<MqttPacket> packetQueue;
    private Consumer<MqttPacket> packetProcessor;

    public PacketConsumer() {
        this(new LinkedBlockingQueue<>());
    }

    public PacketConsumer(BlockingQueue<MqttPacket> packetQueue) {
        this.packetQueue = packetQueue;
    }

    public PacketConsumer(Consumer<MqttPacket> packetProcessor) {
        this();
        this.packetProcessor = packetProcessor;
    }

    public BlockingQueue<MqttPacket> getPacketQueue() {
        return packetQueue;
    }

    public Consumer<MqttPacket> getPacketProcessor() {
        return packetProcessor;
    }

    public void setPacketProcessor(Consumer<MqttPacket> packetProcessor) {
        this.packetProcessor = packetProcessor;
    }

    @Override
    public void run() {
        LOG.debug("Starting packet consumer");
        while (!Thread.currentThread().isInterrupted()) {
            MqttPacket packet;
            try {
                packet = packetQueue.take();
                process(packet);
            } catch (InterruptedException e) {
                break;
            }
        }
        LOG.debug("Ending packet consumer");
    }

    private void process(MqttPacket packet) {
        if (packetProcessor != null) {
            packetProcessor.accept(packet);
        }
    }
}
