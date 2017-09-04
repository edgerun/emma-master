package at.ac.tuwien.dsg.emma.io;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Decode.
 */
public final class Decode {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static int readVariableInt(ByteBuffer buf) {
        byte b;
        int val = 0;
        short pos = 0;

        do {
            b = buf.get();
            val += (b & 0b01111111) << (pos * 7);
            pos++;
        } while ((b & 0b10000000) != 0);// check continuation bit

        return val;
    }

    public static int readTwoByteInt(ByteBuffer buf) {
        byte msb = buf.get();
        byte lsb = buf.get();

        return ((msb << 8) & 0xff00) | (lsb & 0x00ff);
    }

    public static int readTwoByteInt(byte[] buf) {
        return ((buf[0] << 8) & 0xff00) | (buf[1] & 0x00ff);
    }

    public static String readLengthEncodedString(ByteBuffer buf) {
        int len = readTwoByteInt(buf);

        byte[] str = new byte[len];
        buf.get(str);

        return new String(str, UTF8);
    }

    public static InetAddress readInetAddress(ByteBuffer buf) {
        byte[] addr = new byte[4];

        buf.get(addr);

        try {
            return InetAddress.getByAddress(addr);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Decode() {

    }
}
