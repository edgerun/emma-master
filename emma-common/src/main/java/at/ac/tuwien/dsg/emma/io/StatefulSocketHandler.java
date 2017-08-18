package at.ac.tuwien.dsg.emma.io;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

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

    @Override
    protected void doClose(SocketChannel channel, SelectionKey key) {
        super.doClose(channel, key);
        this.channel = null;
        this.key = null;
    }
}
