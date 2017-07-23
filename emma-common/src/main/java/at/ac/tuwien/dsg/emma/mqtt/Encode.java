package at.ac.tuwien.dsg.emma.mqtt;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Encode.
 */
public final class Encode {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public static void writeHeader(ByteBuffer buf, int type) {
        int t = (type << 4) & 0b11110000;
        buf.put((byte) t);
    }

    public static void writeHeader(ByteBuffer buf, int type, int flags) {
        int t = (type << 4) & 0b11110000;
        int f = flags & 0b00001111;

        buf.put((byte) (t | f));
    }

    public static void writeLengthEncodedString(ByteBuffer buf, String str) {
        byte[] encoded = str.getBytes(UTF8);
        int len = encoded.length;

        writeTwoByteInt(buf, len);

        buf.put(encoded);
    }

    public static void writeOneByteInt(ByteBuffer buf, int i) {
        buf.put((byte) (i & 0xff));
    }

    public static void writeTwoByteInt(ByteBuffer buf, int i) {
        buf.put((byte) ((i >> 8) & 0xff)); // msb
        buf.put((byte) (i & 0xff)); // lsb
    }

    public static void writeVariableInt(ByteBuffer buf, int i) {
        if (i < 127) {
            buf.put((byte) (i & 0b01111111));
            return;
        }

        do {
            int b = (i & 0b01111111);
            i = i / 128;

            if (i > 0) {
                b = (b | 0b10000000);
            }

            buf.put((byte) b);

        } while (i > 0);

    }

    private Encode() {

    }


}
