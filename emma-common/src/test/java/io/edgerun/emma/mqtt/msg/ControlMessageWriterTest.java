package io.edgerun.emma.mqtt.msg;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import io.edgerun.emma.io.Decode;
import io.edgerun.emma.mqtt.ConnectFlags;
import io.edgerun.emma.mqtt.ControlPacketType;

/**
 * ControlMessageWriterTest.
 */
public class ControlMessageWriterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void connectMessage() throws Exception {
        byte[] expected = new byte[]{16, 29, 0, 6, 77, 81, 73, 115, 100, 112, 3, 2, 0, 60, 0, 15, 112, 114, 111, 120, 121, 95, 99, 108, 105, 101, 110, 116, 95, 48, 50};
        ByteBuffer actual = ByteBuffer.allocate(1024);

        ConnectMessage msg = new ConnectMessage();
        msg.setProtocolName("MQIsdp");
        msg.setProtocolLevel(3);
        msg.setClientId("proxy_client_02");
        msg.setConnectFlags(ConnectFlags.CLEAN_SESSION);
        msg.setKeepAlive(60);


        Path tempFile = temp.newFile().toPath();

        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            new ControlMessageWriter().write(channel, msg);
            channel.position(0);
            channel.read(actual);
        }

        byte[] actualBytes = new byte[actual.position()];
        actual.flip();
        actual.get(actualBytes);
        assertArrayEquals(expected, actualBytes);
    }

    @Test
    public void publishMessage() throws Exception {
        ByteBuffer expected = ByteBuffer.wrap(new byte[]{48, 10, 0, 4, 116, 101, 115, 116, 97, 115, 100, 102});
        ByteBuffer actual = ByteBuffer.allocate(expected.capacity() * 2);

        PublishMessage msg = new PublishMessage();
        msg.setTopic("test");
        msg.setPayload("asdf".getBytes());
        // default values
        // msg.setQos(0);
        // msg.setRetain(false);
        // msg.setDup(false);


        Path tempFile = temp.newFile().toPath();

        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            new ControlMessageWriter().write(channel, msg);
            channel.position(0);
            channel.read(actual);
        }

        assertEquals(actual.position(), expected.capacity());

        byte[] actualByte = new byte[expected.capacity()];
        actual.flip();
        actual.get(actualByte);

        assertArrayEquals(expected.array(), actualByte);
    }

    @Test
    public void packetIdentifierMessage() throws Exception {
        PacketIdentifierMessage msg = new PacketIdentifierMessage(ControlPacketType.PUBACK, 0xf);
        ByteBuffer actual = ByteBuffer.allocate(32);

        Path tempFile = temp.newFile().toPath();
        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
            new ControlMessageWriter().write(channel, msg);
            channel.position(0);
            channel.read(actual);
        }

        actual.flip();
        assertEquals(ControlPacketType.PUBACK.toHeader(), actual.get());
        assertEquals(2, actual.get());
        assertEquals(0xf, Decode.readTwoByteInt(actual));
        assertEquals(false, actual.hasRemaining());
    }

    //    @Test
    //    public void connectIntegrationTest() throws Exception {
    //        MqttPacketScanner scanner = new MqttPacketScanner(packet -> {
    //            System.out.println("Received packet " + packet);
    //        });
    //
    //        ControlMessageWriter writer = new ControlMessageWriter();
    //
    //        try (SocketChannel channel = SocketChannel.open(new InetSocketAddress(1883))) {
    //
    //            ConnectMessage msg = new ConnectMessage();
    //            msg.setClientId("junit-test");
    //            msg.setConnectFlags(ConnectFlags.CLEAN_SESSION);
    //            msg.setProtocolName("MQIsdp");
    //            msg.setProtocolLevel(3);
    //
    //            writer.write(channel, msg);
    //
    //            PublishMessage pub = new PublishMessage();
    //            pub.setTopic("test");
    //            pub.setQos(0);
    //            pub.setPayload("asdf".getBytes());
    //
    //            writer.write(channel, pub);
    //
    //            SimpleMessage disconnect = SimpleMessage.DISCONNECT;
    //            writer.write(channel, disconnect);
    //        }
    //
    //
    //    }
}