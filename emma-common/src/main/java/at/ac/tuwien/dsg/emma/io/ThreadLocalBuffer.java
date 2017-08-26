package at.ac.tuwien.dsg.emma.io;

import java.nio.ByteBuffer;

/**
 * Uses a {@link ThreadLocal} to store a ByteBuffer and provides handy access methods to it.
 */
public class ThreadLocalBuffer {

    private ThreadLocal<ByteBuffer> threadLocal;

    private ThreadLocalBuffer(ThreadLocal<ByteBuffer> threadLocal) {
        this.threadLocal = threadLocal;
    }

    public ByteBuffer get() {
        return threadLocal.get();
    }

    public ByteBuffer getClean() {
        ByteBuffer byteBuffer = threadLocal.get();
        byteBuffer.clear();
        return byteBuffer;
    }

    public static ThreadLocalBuffer create(int capacity) {
        return new ThreadLocalBuffer(ThreadLocal.withInitial(() -> ByteBuffer.allocate(capacity)));
    }
}
