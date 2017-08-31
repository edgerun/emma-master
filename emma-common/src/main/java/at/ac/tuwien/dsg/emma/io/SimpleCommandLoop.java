package at.ac.tuwien.dsg.emma.io;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * SimpleCommandLoop.
 */
public class SimpleCommandLoop extends AbstractCommandLoop {

    public SimpleCommandLoop() throws IOException {
        super();
    }

    @Override
    protected final void processKey(SelectionKey key) {
        ChannelHandler handler = (ChannelHandler) key.attachment();
        if (handler != null) {
            try {
                handler.handle(key.channel(), key);
            } catch (IOException e) {
                handler.onException(e);
            }
        }
    }

}
