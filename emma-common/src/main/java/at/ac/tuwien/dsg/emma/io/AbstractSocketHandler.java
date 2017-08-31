package at.ac.tuwien.dsg.emma.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * AbstractSocketHandler.
 */
public abstract class AbstractSocketHandler implements ChannelHandler<SocketChannel> {

    @Override
    public void handle(SocketChannel channel, SelectionKey key) throws IOException {
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
        } catch (ClosedChannelException | EOFException e) {
            doClose(channel, key);
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

    protected void doClose(SocketChannel channel, SelectionKey key) {
        if (key != null) {
            key.attach(null);
            key.cancel();
        }
        IOUtils.close(channel);
    }

    @Override
    public void onException(IOException exception) {
        exception.printStackTrace(System.err);
    }
}
