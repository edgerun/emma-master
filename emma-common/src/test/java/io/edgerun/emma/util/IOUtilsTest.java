package io.edgerun.emma.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
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

    @Test
    public void copyRemaining_behavesCorrectly() throws Exception {
        ByteBuffer original = ByteBuffer.allocate(1024);

        original.put((byte) 1);
        original.put((byte) 2);
        original.put((byte) 3);
        original.put((byte) 4);

        original.flip();

        original.get(); // consume 1
        original.get(); // consume 2

        int position = original.position();
        long remaining = original.remaining();

        ByteBuffer copy = IOUtils.copyRemaining(original);

        // original remains unchanged
        assertEquals(position, original.position());
        assertEquals(remaining, original.remaining());


        assertEquals(3, copy.get());
        assertEquals(4, copy.get());
        assertFalse(copy.hasRemaining());
    }

    @Test
    public void copyRemaining_onEmptyBuffer_behavesCorrectly() throws Exception {
        ByteBuffer original = ByteBuffer.allocate(1024);

        original.put((byte) 1);
        original.put((byte) 2);

        original.flip();

        original.get();
        original.get();

        ByteBuffer copy = IOUtils.copyRemaining(original);
        assertFalse(copy.hasRemaining());
    }

    @Test
    public void copyRemaining_withArray_behavesCorrectly() throws Exception {
        ByteBuffer o1 = ByteBuffer.allocate(1024);
        ByteBuffer o2 = ByteBuffer.allocate(1024);

        o1.put((byte) 1);
        o1.put((byte) 2);
        o2.put((byte) 3);
        o2.put((byte) 4);
        o1.flip();
        o2.flip();


        o1.get();
        o1.get();
        o2.get();

        ByteBuffer copy = IOUtils.copyRemaining(new ByteBuffer[]{o1, o2});
        assertTrue(copy.hasRemaining());
        assertEquals(4, copy.get());
        assertFalse(copy.hasRemaining());
    }

}