package at.ac.tuwien.dsg.emma.manager.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.ClientRepository;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.network.sel.BrokerSelectionStrategy;

/**
 * ReconnectEngine.
 */
@Service
public class ReconnectEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ReconnectEngine.class);

    private NetworkManager networkManager;
    private ClientRepository clientRepository;
    private MonitoringService monitoringService;
    private BrokerSelectionStrategy strategy;

    @Scheduled(fixedDelay = 30000)
    public void scheduled() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Scheduled reconnect run");
        }

        Instant now = Instant.now();

        int reconnectThreshold = 10;

        for (Client client : clientRepository.getHosts().values()) {
            if (client.getLastReconnect() != null) {
                if (client.getLastReconnect().until(now, ChronoUnit.MILLIS) < reconnectThreshold) {
                    continue;
                }
            }

            Broker broker = determineBroker(client);
            if (Objects.equals(broker, client.getConnectedTo())) {
                continue;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Instructing {} to reconnect to {}", client, broker);
            }
            monitoringService.reconnect(client, broker);
        }
    }

    public Broker determineBroker(Client client) {
        return strategy.select(client, networkManager.getNetwork());
    }

    @Autowired
    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setMonitoringService(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Autowired
    public void setStrategy(BrokerSelectionStrategy strategy) {
        this.strategy = strategy;
    }
}
