package at.ac.tuwien.dsg.emma.io;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * ChannelHandler.
 */
public interface ChannelHandler<T extends Channel> {

    void handle(T channel, SelectionKey key) throws IOException;

    default void onException(IOException exception) {
        // UGLY
    }
}
