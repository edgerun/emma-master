package io.edgerun.emma.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import io.edgerun.emma.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerSocketListener implements Runnable, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ServerSocketListener.class);

    private InetSocketAddress bind;
    private Consumer<SocketChannel> acceptHandler;

    private ServerSocketChannel serverSocket;
    private CommandLoop commandLoop;

    public ServerSocketListener(InetSocketAddress bind, Consumer<SocketChannel> acceptHandler) {
        this.bind = bind;
        this.acceptHandler = acceptHandler;
    }

    @Override
    public void run() {
        try {
            checkedRun();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public CommandLoop getCommandLoop() {
        return commandLoop;
    }

    private void checkedRun() throws IOException {
        commandLoop = new SimpleCommandLoop();

        serverSocket = ServerSocketChannel.open();
        LOG.info("Binding server socket to {}", bind);
        serverSocket.bind(bind);
        serverSocket.configureBlocking(false);

        commandLoop.register(serverSocket, new AcceptHandler(acceptHandler));

        commandLoop.run();
    }

    @Override
    public void close() {
        IOUtils.close(serverSocket, commandLoop);
    }

    private static class AcceptHandler implements ChannelHandler<ServerSocketChannel> {

        private Consumer<SocketChannel> socketConsumer;

        public AcceptHandler(Consumer<SocketChannel> socketConsumer) {
            this.socketConsumer = socketConsumer;
        }

        @Override
        public void handle(ServerSocketChannel serverSocket, SelectionKey key) throws IOException {
            if (!key.isAcceptable()) {
                return;
            }

            SocketChannel accept = (((ServerSocketChannel) key.channel())).accept();
            if (accept == null) {
                return;
            }

            accept.configureBlocking(false);
            accept.finishConnect();

            socketConsumer.accept(accept);
        }
    }
}
