package at.ac.tuwien.dsg.emma.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * DatagramLoop.
 */
public abstract class DatagramLoop implements Runnable, Closeable, ChannelHandler<DatagramChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(DatagramLoop.class);

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

    public InetSocketAddress getChannelAddress() {
        if (channel == null) {
            return null;
        }
        try {
            return (InetSocketAddress) channel.getLocalAddress();
        } catch (IOException e) {
            return null;
        }
    }

    private void runChecked() throws IOException {
        loop = new SimpleCommandLoop();

        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        LOG.info("Opening DatagramChannel on {}", bind);
        channel.bind(bind);

        key = loop.register(channel, SelectionKey.OP_READ, this);

        loop.run();
    }

    @Override
    public void close() {
        IOUtils.close(loop, channel);
    }
}