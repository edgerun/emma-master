package at.ac.tuwien.dsg.emma.mqtt;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import at.ac.tuwien.dsg.emma.mqtt.msg.ConnackMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.ConnectMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.ConnectReturnCode;
import at.ac.tuwien.dsg.emma.mqtt.msg.ControlMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.ControlMessageWriter;
import at.ac.tuwien.dsg.emma.mqtt.msg.MqttPacketParser;
import at.ac.tuwien.dsg.emma.mqtt.msg.PacketIdentifierMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.PublishMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.QoS;
import at.ac.tuwien.dsg.emma.mqtt.msg.SimpleMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.SubackMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.SubscribeMessage;
import at.ac.tuwien.dsg.emma.mqtt.msg.UnsubscribeMessage;

/**
 * Tests message writing/parsing in one go.
 */
public class MessageTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private MqttPacketScanner scanner;
    private MqttPacketParser parser;
    private ControlMessageWriter writer;

    private List<MqttPacket> packets;

    private Path tempFile;

    @Before
    public void setUp() throws Exception {
        packets = new ArrayList<>();
        scanner = new MqttPacketScanner(packets::add);
        parser = new MqttPacketParser();
        writer = new ControlMessageWriter();
        tempFile = temp.newFile().toPath();
    }

    @Test
    public void connectMessage() throws Exception {
        ConnectMessage msg = new ConnectMessage();
        msg.setProtocolName("MQTT");
        msg.setProtocolLevel(3);
        msg.setConnectFlags(ConnectFlags.CLEAN_SESSION);
        msg.setKeepAlive(60);
        msg.setClientId("junit-test");

        ConnectMessage readMsg = writeAndParse(msg);

        assertEquals("MQTT", readMsg.getProtocolName());
        assertEquals(3, readMsg.getProtocolLevel());
        assertTrue(readMsg.hasCleanSession());
        assertFalse(readMsg.hasWill());
        assertEquals(60, readMsg.getKeepAlive());
        assertEquals("junit-test", readMsg.getClientId());
    }

    @Test
    public void connackMessage() throws Exception {
        ConnackMessage msg = new ConnackMessage(true, ConnectReturnCode.NOT_AUTHORIZED);

        ConnackMessage readMsg = writeAndParse(msg);

        assertEquals(true, readMsg.isSessionPresent());
        assertEquals(ConnectReturnCode.NOT_AUTHORIZED, readMsg.getReturnCode());
    }

    @Test
    public void subscribeMessage() throws Exception {
        SubscribeMessage msg = new SubscribeMessage(42,
                Arrays.asList("a", "b/b", "c/c/c"),
                Arrays.asList(QoS.AT_MOST_ONCE, QoS.AT_LEAST_ONCE, QoS.EXACTLY_ONCE)
        );

        SubscribeMessage readMessage = writeAndParse(msg);

        assertEquals(42, readMessage.getPacketId());
        assertEquals(Arrays.asList("a", "b/b", "c/c/c"), readMessage.getFilter());
        assertEquals(Arrays.asList(QoS.AT_MOST_ONCE, QoS.AT_LEAST_ONCE, QoS.EXACTLY_ONCE), readMessage.getRequestedQos());
    }

    @Test
    public void subackMessage() throws Exception {
        SubackMessage msg = new SubackMessage(42, new QoS[]{QoS.AT_MOST_ONCE, null, QoS.AT_LEAST_ONCE, QoS.EXACTLY_ONCE});

        SubackMessage readMsg = writeAndParse(msg);

        assertEquals(42, readMsg.getPacketId());
        assertEquals(4, readMsg.getFilterQos().length);
        assertEquals(QoS.AT_MOST_ONCE, readMsg.getFilterQos()[0]);
        assertEquals(null, readMsg.getFilterQos()[1]);
        assertEquals(QoS.AT_LEAST_ONCE, readMsg.getFilterQos()[2]);
        assertEquals(QoS.EXACTLY_ONCE, readMsg.getFilterQos()[3]);
    }

    @Test
    public void unsubscribeMessage() throws Exception {
        UnsubscribeMessage msg = new UnsubscribeMessage(42, Arrays.asList("a", "b/b"));

        UnsubscribeMessage readMsg = writeAndParse(msg);

        assertEquals(42, readMsg.getPacketId());
        assertEquals(Arrays.asList("a", "b/b"), readMsg.getTopics());
    }

    @Test
    public void publishMessage() throws Exception {
        PublishMessage msg = new PublishMessage();
        msg.setPacketId(42);
        msg.setQos(1);
        msg.setPayload("my payload".getBytes());
        msg.setTopic("junit");
        // msg.setDup(false); // default value
        msg.setRetain(true);

        PublishMessage readMsg = writeAndParse(msg);

        assertEquals(42, readMsg.getPacketId());
        assertEquals(1, readMsg.getQos());
        assertEquals("my payload", new String(readMsg.getPayload()));
        assertEquals("junit", readMsg.getTopic());
        assertEquals(false, readMsg.isDup());
        assertEquals(true, readMsg.isRetain());
    }

    @Test
    public void pubackMessage() throws Exception {
        PacketIdentifierMessage msg = new PacketIdentifierMessage(ControlPacketType.PUBACK, 42);

        PacketIdentifierMessage readMsg = writeAndParse(msg);

        assertEquals(ControlPacketType.PUBACK, readMsg.getControlPacketType());
        assertEquals(42, readMsg.getPacketIdentifier());
    }

    @Test
    public void pingReqMsesage() throws Exception {
        SimpleMessage msg = SimpleMessage.PINGREQ;

        SimpleMessage readMsg = writeAndParse(msg);

        assertEquals(ControlPacketType.PINGREQ, readMsg.getControlPacketType());
    }

    // TODO

    private <T extends ControlMessage> T writeAndParse(T msg) throws IOException {

        try (FileChannel channel = FileChannel.open(tempFile, WRITE)) {
            writer.writeControlMessage(channel, msg);
        }

        try (FileChannel channel = FileChannel.open(tempFile, READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 100);
            channel.read(buffer);
            buffer.flip();
            scanner.read(buffer);
        }

        assertEquals(1, packets.size());

        ControlMessage controlMessage = parser.parse(packets.get(0));
        System.out.println(controlMessage.toString());
        return (T) controlMessage;

    }
}
