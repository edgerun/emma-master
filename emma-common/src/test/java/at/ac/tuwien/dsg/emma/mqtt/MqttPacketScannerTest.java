package at.ac.tuwien.dsg.emma.mqtt;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * MqttPacketScannerTest.
 */
public class MqttPacketScannerTest {
    @Test
    public void testFragmentedRead_behavesCorrectly() throws Exception {
        byte[] packet = new byte[]{
                16, 29, 0, 6, 77, 81, 73, 115, 100, 112, 3, 2, 0, 60, 0, 15, 112, 114, 111, 120, 121, 95, 99, 108, 105, 101, 110, 116, 95, 48, 50
        };

        byte[] frag1 = new byte[]{16, 29, 0, 6, 77, 81, 73, 115};
        byte[] frag2 = new byte[]{100, 112, 3, 2, 0, 60, 0, 15, 112, 114, 111, 120};
        byte[] frag3 = new byte[]{121, 95, 99, 108, 105, 101, 110, 116, 95, 48, 50, /* next packet */
                ControlPacketType.DISCONNECT.toHeader(),
                0
        };

        ByteBuffer buf1 = ByteBuffer.wrap(frag1);
        ByteBuffer buf2 = ByteBuffer.wrap(frag2);
        ByteBuffer buf3 = ByteBuffer.wrap(frag3);

        List<MqttPacket> packets = new ArrayList<>();

        MqttPacketScanner scanner = new MqttPacketScanner(p -> {
            System.out.println("Got packet " + p);
            packets.add(p);
        });

        scanner.read(buf1);
        scanner.read(buf2);
        scanner.read(buf3);

        assertEquals(2, packets.size());

        assertArrayEquals(packet, packets.get(0).asBuffer().array());
        assertArrayEquals(new byte[]{ControlPacketType.DISCONNECT.toHeader(), 0}, packets.get(1).asBuffer().array());
    }

    @Test
    public void testFragmentedRemainingLength_behavesCorrectly() throws Exception {
        byte[] data = new byte[128];

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i & 0xff);
        }

        ByteBuffer packet = ByteBuffer.allocate(1 + 2 + data.length);

        ByteBuffer p1 = ByteBuffer.allocate(2);
        ByteBuffer p2 = ByteBuffer.allocate(1 + data.length);

        p1.put(ControlPacketType.CONNECT.toHeader());
        p1.put((byte) 0b10000000); // cont
        p2.put((byte) 0b00000001); // 128
        p2.put(data);

        p1.flip();
        p2.flip();

        MqttPacketScanner scanner = new MqttPacketScanner(p -> {
            System.out.println(p);

            assertEquals(128, p.getRemLen());
            assertEquals(ControlPacketType.CONNECT.toHeader(), p.getHeader());
            assertEquals(128, p.getData().length);
            assertEquals(127, p.getData()[127]);
        });

        scanner.read(p1);
        scanner.read(p2);
    }
}