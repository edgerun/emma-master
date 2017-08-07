package at.ac.tuwien.dsg.emma.manager.broker;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.io.Decode;
import at.ac.tuwien.dsg.emma.util.IOUtils;

/**
 * KeepaliveReceiver.
 */
@Component
public class KeepaliveReceiver implements Runnable, InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(KeepaliveReceiver.class);

    @Autowired
    private BrokerRepository brokers;

    private int port;

    private Thread thread;
    private DatagramSocket socket;

    public KeepaliveReceiver() {
        this(60042);
    }

    private KeepaliveReceiver(int port) {
        // TODO parameterize
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        socket = new DatagramSocket(port);
        thread = new Thread(this, "emma.KeepaliveReceiver");
        thread.start();
    }

    @Override
    public void run() {
        LOG.debug("Starting KeepaliveReceiver");
        DatagramPacket packet = new DatagramPacket(new byte[2], 2);

        while (true) {
            try {
                socket.receive(packet);

                int brokerPort = Decode.readTwoByteInt(packet.getData());
                String id = packet.getAddress().getHostAddress() + ":" + brokerPort;

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Received keepalive packet for {}", id);
                }

                BrokerInfo brokerInfo = brokers.getBrokerInfo(id);

                if (brokerInfo != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Updating alive status for broker {}", brokerInfo);
                    }
                    brokerInfo.setAlive(true);
                    brokerInfo.setLastSeen(System.currentTimeMillis());
                }

            } catch (SocketException e) {
                break;
            } catch (IOException e) {
                LOG.error("IOException while listening on UDP socket", e);
            }
        }
        LOG.debug("Stopped KeepAliveReceiver");
    }

    @Override
    public void destroy() throws Exception {
        IOUtils.close(socket);
        thread.interrupt();
    }
}
