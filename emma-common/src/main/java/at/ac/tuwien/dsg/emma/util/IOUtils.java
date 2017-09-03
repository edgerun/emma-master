package at.ac.tuwien.dsg.emma.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * IOUtils.
 */
public final class IOUtils {

    public static final String LN = System.lineSeparator();

    private IOUtils() {
        // util class
    }

    public static boolean isConnectionReset(IOException e) {
        String message = e.getMessage();
        return message != null && message.equals("Connection reset by peer");
    }

    public static boolean isConnectionReset(UncheckedIOException e) {
        return isConnectionReset(e.getCause());
    }

    public static boolean isBrokenPipe(IOException e) {
        String message = e.getMessage();
        return message != null && message.equals("Broken pipe");
    }

    public static boolean isBrokenPipe(UncheckedIOException e) {
        return isBrokenPipe(e.getCause());
    }

    public static String read(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            return read(in);
        }
    }

    public static String read(InputStream stream) {
        return new Scanner(stream, "UTF-8").useDelimiter("\\A").next();
    }

    public static void cancel(SelectionKey key) {
        if (key != null) {
            key.cancel();
            key.attach(null);
        }
    }

    public static void shutdown(SocketChannel channel) {
        if (channel == null) {
            return;
        }
        if (!channel.isOpen()) {
            return;
        }

        try {
            channel.shutdownInput();
            channel.shutdownOutput();
        } catch (IOException e) {
            // swallow
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

    public static ByteBuffer copyRemaining(ByteBuffer original) {
        ByteBuffer buf = ByteBuffer.allocate(original.remaining());
        original.mark();
        buf.put(original);
        original.reset();
        buf.flip();
        return buf;
    }

    public static ByteBuffer copyRemaining(ByteBuffer[] originals) {
        int remaining = 0;
        for (ByteBuffer original : originals) {
            remaining += original.remaining();
        }
        ByteBuffer copy = ByteBuffer.allocate(remaining);

        for (ByteBuffer original : originals) {
            original.mark();
            copy.put(original);
            original.reset();
        }

        copy.flip();
        return copy;
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
        return "SelectionKey@" + key.hashCode() + " { " + key.channel() + LN +
                "   isValid: " + key.isValid() + LN +
                "   isAcceptable: " + key.isAcceptable() + LN +
                "   isConnectable: " + key.isConnectable() + LN +
                "   isReadable: " + key.isReadable() + LN +
                "   isWritable: " + key.isWritable() + LN +
                "}";
    }
}
