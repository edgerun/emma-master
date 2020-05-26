package io.edgerun.emma.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * CountingLatch.
 */
public class CountingLatch {

    private final AtomicInteger count;

    public CountingLatch() {
        this.count = new AtomicInteger(0);
    }

    public void await() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        while (count.get() > 0) {
            synchronized (count) {
                count.wait();
            }
        }
    }

    public int incr() {
        return count.incrementAndGet();
    }

    public int decr() {
        try {
            return count.decrementAndGet();
        } finally {
            synchronized (count) {
                count.notifyAll();
            }
        }
    }

    public int get() {
        return count.get();
    }
}