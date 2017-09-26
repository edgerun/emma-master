package at.ac.tuwien.dsg.emma.controller.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.emma.controller.network.NetworkManager;
import at.ac.tuwien.dsg.emma.controller.network.balancing.BalancingOperation;
import at.ac.tuwien.dsg.emma.controller.network.balancing.BalancingStrategy;
import at.ac.tuwien.dsg.emma.util.Concurrent;

@Service
public class BalancingEngine {

    private static final Logger LOG = LoggerFactory.getLogger(BalancingEngine.class);

    private NetworkManager networkManager;
    private MonitoringService monitoringService;
    private BalancingStrategy balancingStrategy;

    @Scheduled(fixedDelay = 15000) // will never be executed in parallel
    public void scheduled() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Scheduled reconnect run");
        }

        List<BalancingOperation> operations = balancingStrategy.balance(networkManager.getNetwork());

        for (BalancingOperation operation : operations) {
            monitoringService.reconnect(operation.getClient(), operation.getTarget());
            Concurrent.sleep(5); // FIXME hacky
        }
    }

    @Autowired
    public void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    @Autowired
    public void setMonitoringService(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @Autowired
    public void setBalancingStrategy(BalancingStrategy balancingStrategy) {
        this.balancingStrategy = balancingStrategy;
    }
}
