package at.ac.tuwien.dsg.emma.io;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.ac.tuwien.dsg.emma.util.Concurrent;

/**
 * ConcurrentCommandLoop. This may reduce performance immensely for really short task.
 */
public class ConcurrentCommandLoop extends AbstractCommandLoop {

    private ExecutorService executor;

    public ConcurrentCommandLoop(int concurrency) throws IOException {
        this(Executors.newFixedThreadPool(concurrency));
    }

    public ConcurrentCommandLoop(ExecutorService executor) throws IOException {
        super();
        this.executor = executor;
    }

    public ConcurrentCommandLoop(Selector selector, ExecutorService executor) {
        super(selector);
        this.executor = executor;
    }

    @Override
    protected final void processKey(SelectionKey key) {
        executor.submit(new ExecutionRequest(key, (ChannelHandler) key.attachment()));
    }

    @Override
    public void close() throws IOException {
        super.close();
        Concurrent.shutdownAndAwaitTermination(executor);
    }

    private static class ExecutionRequest implements Callable<Void> {
        SelectionKey key;
        ChannelHandler handler;

        public ExecutionRequest(SelectionKey key, ChannelHandler handler) {
            this.key = key;
            this.handler = handler;
        }

        @Override
        public Void call() throws Exception {
            try {
                handler.handle(key.channel(), key);
            } catch (IOException e) {
                handler.onException(e);
                throw e;
            }

            return null;
        }
    }
}
