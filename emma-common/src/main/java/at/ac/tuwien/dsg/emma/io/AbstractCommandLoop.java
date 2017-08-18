package at.ac.tuwien.dsg.emma.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.Instant;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * AbstractCommandLoop.
 */
public abstract class AbstractCommandLoop implements CommandLoop {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCommandLoop.class);

    private final Object selectLock = new Object();

    protected Selector selector;

    public AbstractCommandLoop() throws IOException {
        this(Selector.open());
    }

    protected AbstractCommandLoop(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (selector.isOpen() && !Thread.currentThread().isInterrupted()) {
                synchronized (selectLock) {
                    // https://stackoverflow.com/questions/12822298/nio-selector-how-to-properly-register-new-channel-while-selecting
                }

                int keys = selector.select();

                if (LOG.isTraceEnabled()) {
                    LOG.trace("{} selected {} keys at {}", Thread.currentThread().getName(), keys, Instant.now());
                }

                if (keys <= 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Processing key {}", IOUtils.toString(key));
                    }
                    processKey(key);
                    iterator.remove();
                }
            }
        } catch (ClosedSelectorException e) {
            // exit loop gracefully
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <C extends SelectableChannel> SelectionKey register(C channel, ChannelHandler<C> handler) {
        return register(channel, channel.validOps(), handler);
    }

    @Override
    public <C extends SelectableChannel> SelectionKey register(C channel, int ops, ChannelHandler<C> handler) {
        synchronized (selectLock) {
            try {
                selector.wakeup();
                return channel.register(selector, ops, handler);
            } catch (ClosedChannelException e) {
                // do nothing
            }
            return null;
        }
    }

    protected abstract void processKey(SelectionKey key);

    @Override
    public Selector getSelector() {
        return selector;
    }

    @Override
    public void close() throws IOException {
        IOUtils.close(selector);
    }
}
