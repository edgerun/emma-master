package at.ac.tuwien.dsg.emma.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Concurrent.
 */
public final class Concurrent {

    private Concurrent() {
        // util class
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // squelch
        }
    }

    public static void join(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            // squelch
        }
    }

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
