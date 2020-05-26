package io.edgerun.emma.io;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import io.edgerun.emma.util.IOUtils;

/**
 * StatefulSocketHandler.
 */
public abstract class StatefulSocketHandler extends AbstractSocketHandler {

    protected SocketChannel channel;
    protected SelectionKey key;

    public StatefulSocketHandler(SocketChannel channel) {
        this.channel = channel;
    }

    public void register(CommandLoop commandLoop) {
        this.key = commandLoop.register(channel, this);
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public SelectionKey getKey() {
        return key;
    }

    public void shutdown() {
        IOUtils.shutdown(channel);
    }

    @Override
    protected void cleanup(SocketChannel channel, SelectionKey key) {
        this.channel = null;
        this.key = null;
    }
}
