package at.ac.tuwien.dsg.emma.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * IOUtils.
 */
public final class IOUtils {
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
}
