package io.edgerun.emma.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import io.edgerun.emma.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractSocketHandler.
 */
public abstract class AbstractSocketHandler implements ChannelHandler<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSocketHandler.class);

    @Override
    public void handle(SocketChannel channel, SelectionKey key) {
        try {
            if (key.isConnectable()) {
                doConnect(channel, key);
                if (!key.isValid()) {
                    return;
                }
            }

            if (key.isReadable()) {
                doRead(channel, key);
            }

            if (key.isValid() && key.isWritable()) {
                doWrite(channel, key);
            }
        } catch (EOFException e) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("EOFException received {}", channel);
                }

                onEofException(channel, key);
            } finally {
                cleanup(channel, key);
            }
            return;
        } catch (ClosedChannelException e) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Channel was closed {}", channel, e);
                }
                onClosedChannelException(channel, key);
            } finally {
                cleanup(channel, key);
            }
        } catch (IOException e) {
            onException(channel, key, e);
        }
    }

    private void onException(SocketChannel channel, SelectionKey key, IOException e) {
        if (IOUtils.isBrokenPipe(e)) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Ungraceful close: broken pipe {}", channel, e);
            } else {
                LOG.warn("Ungraceful close: broken pipe {}", channel);
            }
        } else if (IOUtils.isConnectionReset(e)) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Ungraceful close: connection reset by peer {}", channel, e);
            } else {
                LOG.warn("Ungraceful close: connection reset by peer {}", channel);
            }
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.warn("IO exception during key handling", e);
            } else {
                LOG.warn("IO exception during key handling: {}", e.getMessage());
            }
        }

        IOUtils.cancel(key);
        IOUtils.close(channel);
        cleanup(channel, key);
    }

    @Override
    public void onException(IOException exception) {
        if (LOG.isDebugEnabled()) {
            LOG.warn("IO exception during key handling", exception);
        } else {
            LOG.warn("IO exception during key handling: {}", exception.getMessage());
        }
    }

    protected void doConnect(SocketChannel channel, SelectionKey key) throws IOException {
        if (channel.finishConnect()) {
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    protected abstract void doRead(SocketChannel channel, SelectionKey key) throws IOException;

    protected void doWrite(SocketChannel channel, SelectionKey key) throws IOException {
        // default implementation simply avoids rapid invocations (channels are almost always writable)
        key.interestOps(SelectionKey.OP_READ);
    }

    protected void onClosedChannelException(SocketChannel channel, SelectionKey key) {
        IOUtils.cancel(key);
    }

    protected void onEofException(SocketChannel channel, SelectionKey key) {
        IOUtils.cancel(key);
        IOUtils.close(channel);
    }

    protected void cleanup(SocketChannel channel, SelectionKey key) {
        // hook
    }
}
