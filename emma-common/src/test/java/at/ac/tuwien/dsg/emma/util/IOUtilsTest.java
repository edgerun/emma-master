package at.ac.tuwien.dsg.emma.util;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;


/**
 * IOUtilsTest.
 */
public class IOUtilsTest {
    @Test
    public void close_null_behavesCorrectly() throws Exception {
        Closeable c = null;
        IOUtils.close(c);
    }

    @Test
    public void close_callsCloseCorrectly() throws Exception {
        AtomicInteger calls = new AtomicInteger(0);

        Closeable c = () -> {
            calls.incrementAndGet();
        };

        IOUtils.close(c);

        assertEquals(1, calls.get());
    }

    @Test
    public void close_onException_behavesCorrectly() throws Exception {
        Closeable c = () -> {
            throw new IOException();
        };

        IOUtils.close(c); // does not throw an exception
    }

}