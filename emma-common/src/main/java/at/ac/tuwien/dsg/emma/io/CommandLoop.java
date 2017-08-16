package at.ac.tuwien.dsg.emma.io;

import java.io.Closeable;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * CommandLoop.
 */
public interface CommandLoop extends Runnable, Closeable {

    <C extends SelectableChannel> SelectionKey register(C channel, ChannelHandler<C> handler);

    <C extends SelectableChannel> SelectionKey register(C channel, int ops, ChannelHandler<C> handler);

    Selector getSelector();
}
