package at.ac.tuwien.dsg.emma.mqtt;

import static at.ac.tuwien.dsg.emma.io.Decode.readLengthEncodedString;
import static at.ac.tuwien.dsg.emma.io.Decode.readVariableInt;
import static at.ac.tuwien.dsg.emma.io.Encode.writeLengthEncodedString;
import static at.ac.tuwien.dsg.emma.io.Encode.writeVariableInt;
import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;

import org.junit.Test;

import at.ac.tuwien.dsg.emma.io.Decode;
import at.ac.tuwien.dsg.emma.io.Encode;

/**
 * CodecTest.
 */
public class CodecTest {

    @Test
    public void testVarLengthInt() throws Exception {
        assertEquals(0, readVariableInt(asVariableInt(0)));
        assertEquals(1, readVariableInt(asVariableInt(1)));
        assertEquals(3, readVariableInt(asVariableInt(3)));
        assertEquals(64, readVariableInt(asVariableInt(64)));
        assertEquals(127, readVariableInt(asVariableInt(127)));
        assertEquals(128, readVariableInt(asVariableInt(128)));
        assertEquals(129, readVariableInt(asVariableInt(129)));
        assertEquals(Integer.MAX_VALUE, readVariableInt(asVariableInt(Integer.MAX_VALUE)));
    }

    private ByteBuffer asVariableInt(int i) {
        ByteBuffer buf = ByteBuffer.allocate(5);
        writeVariableInt(buf, i);
        buf.flip();
        return buf;
    }

    @Test
    public void testLengthEncodedString() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(32);

        writeLengthEncodedString(buffer, "foobar");

        assertEquals(8, buffer.position());

        buffer.flip();

        assertEquals("foobar", readLengthEncodedString(buffer));
    }


    @Test
    public void test() throws Exception {
        ByteChannel bc;

        ByteBuffer buf = ByteBuffer.allocate(4);

        buf.put((byte) 1);
        buf.put((byte) 2);
        buf.put((byte) 3);
        buf.put((byte) 4);

        buf.clear();

        buf.put((byte) 5);
        buf.put((byte) 6);

        buf.flip();

        ByteBuffer trans = ByteBuffer.allocate(20);

        trans.put(buf);

        trans.flip();

        byte[] b = new byte[2];

        trans.get(b);

        System.out.println(Arrays.toString(b));


        System.out.println(Arrays.toString(trans.array()));

    }

    @Test
    public void readInetAddress() throws Exception {

        InetAddress address = InetAddress.getByAddress(new byte[]{127, 0, 0, 1});

        ByteBuffer buf = ByteBuffer.allocate(64);

        Encode.writeInetAddress(buf, address);

        buf.flip();
        assertEquals(4, buf.remaining());


        InetAddress actual = Decode.readInetAddress(buf);

        assertEquals("127.0.0.1", actual.getHostAddress());
    }

}