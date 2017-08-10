package at.ac.tuwien.dsg.emma.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * DatagramLoop.
 */
public abstract class DatagramLoop implements Runnable, Closeable, ChannelHandler<DatagramChannel> {

    protected InetSocketAddress bind;

    protected CommandLoop loop;
    protected DatagramChannel channel;
    protected SelectionKey key;

    public DatagramLoop(int port) {
        this(new InetSocketAddress(port));
    }

    public DatagramLoop(InetSocketAddress bind) {
        this.bind = bind;
    }

    @Override
    public void run() {
        try {
            runChecked();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public InetSocketAddress getBindAddress() {
        return bind;
    }

    private void runChecked() throws IOException {
        loop = new SimpleCommandLoop();

        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(bind);

        key = loop.register(channel, SelectionKey.OP_READ, this);

        loop.run();
    }

    @Override
    public void close() {
        IOUtils.close(loop, channel);
    }
}