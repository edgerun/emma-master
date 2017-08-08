package at.ac.tuwien.dsg.emma.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Scanner;

/**
 * IOUtils.
 */
public final class IOUtils {

    public static final String LN = System.lineSeparator();

    private IOUtils() {
        // util class
    }

    public static String read(URL url) throws IOException {
        try (Scanner scanner = new Scanner(url.openStream(), "UTF-8")) {
            return scanner.useDelimiter("\\A").next();
        }
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

    public static String toString(InputStream stream) {
        return new Scanner(stream).useDelimiter("\\A").next();
    }

    public static String toString(ByteBuffer buf) {
        byte[] bytes = new byte[buf.remaining()];

        buf.get(bytes);

        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
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
