package at.ac.tuwien.dsg.emma.manager.network.monitoring;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.io.Decode;
import at.ac.tuwien.dsg.emma.manager.network.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.network.Broker;
import at.ac.tuwien.dsg.emma.util.IOUtils;

@Component
public class HeartbeatListener implements Runnable, InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatListener.class);

    @Autowired
    private BrokerRepository brokers;

    private InetSocketAddress bind;

    private Thread thread;
    private DatagramSocket socket;

    @Autowired
    public HeartbeatListener(
            @Value("${emma.manager.address}") String address,
            @Value("${emma.manager.heartbeat.port}") Integer port) {
        this(new InetSocketAddress(address, port));
    }

    private HeartbeatListener(InetSocketAddress bind) {
        this.bind = bind;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("Opening socket for HeartbeatListener on {}", bind);
        socket = new DatagramSocket(bind); // TODO parameterize
        thread = new Thread(this, "emma.HeartbeatListener");
        thread.start();
    }

    @Override
    public void run() {
        LOG.debug("Starting HeartbeatListener", bind);
        DatagramPacket packet = new DatagramPacket(new byte[2], 2);

        while (true) {
            try {
                socket.receive(packet);

                int brokerPort = Decode.readTwoByteInt(packet.getData());
                String id = packet.getAddress().getHostAddress() + ":" + brokerPort;

                if (LOG.isTraceEnabled()) {
                    LOG.trace("Received heartbeat packet for {}", id);
                }

                Broker brokerInfo = brokers.getBroker(id);

                if (brokerInfo != null) {
                    if (LOG.isDebugEnabled() && !brokerInfo.isAlive()) {
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
        LOG.debug("Stopped HeartbeatListener");
    }

    @Override
    public void destroy() throws Exception {
        IOUtils.close(socket);
        thread.interrupt();
    }
}
