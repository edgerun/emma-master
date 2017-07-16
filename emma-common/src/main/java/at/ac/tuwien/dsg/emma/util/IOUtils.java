package at.ac.tuwien.dsg.emma.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * IOUtils.
 */
public final class IOUtils {

    public static final String LN = System.lineSeparator();

    private IOUtils() {
        // util class
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            close(closeable);
        }
    }

    public static String toString(SelectionKey key) {
        return "SelectionKey { " + key.channel() + LN +
                "   isValid: " + key.isValid() + LN +
                "   isAcceptable: " + key.isAcceptable() + LN +
                "   isConnectable: " + key.isConnectable() + LN +
                "   isReadable: " + key.isReadable() + LN +
                "   isWritable: " + key.isWritable() + LN +
                "}";
    }
}
