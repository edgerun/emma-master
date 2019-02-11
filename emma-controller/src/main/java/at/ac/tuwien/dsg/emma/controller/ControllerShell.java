package at.ac.tuwien.dsg.emma.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.ClientRepository;
import at.ac.tuwien.dsg.emma.controller.network.NetworkManager;
import at.ac.tuwien.dsg.emma.controller.service.MonitoringService;
import at.ac.tuwien.dsg.emma.controller.service.sub.Subscription;
import at.ac.tuwien.dsg.emma.controller.service.sub.SubscriptionTable;
import at.ac.tuwien.dsg.emma.util.NetUtils;
import at.ac.tuwien.dsg.orvell.Context;
import at.ac.tuwien.dsg.orvell.annotation.Command;
import at.ac.tuwien.dsg.orvell.annotation.CommandGroup;

@CommandGroup("emma")
@Component
public class ControllerShell {

    @Autowired
    private Environment environment;

    @Autowired
    private SubscriptionTable subscriptions;

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private NetworkManager networkManager;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BrokerRepository brokerRepository;

    @Command(name = "sub-list")
    public void listSubscriptions(Context ctx) {
        for (Subscription sub : subscriptions.getSubscriptions()) {
            ctx.out().printf("%-16s : %s%n", sub.getBroker().getId(), sub.getFilter());
        }
    }

    @Command(name = "broker-list")
    public void listBrokers(Context ctx) {
        for (Broker broker : brokerRepository.getHosts().values()) {
            ctx.out().println(broker);
        }
    }

    @Command(name = "network")
    public void network(Context ctx) {
        ctx.out().println(networkManager.getNetwork());
    }

    @Command
    public void reconnect(String clientId, String brokerId) {
        Client client = clientRepository.getById(clientId);
        if (client == null) {
            throw new IllegalArgumentException("No such client " + clientId);
        }

        Broker broker = brokerRepository.getById(brokerId);
        if (broker == null) {
            throw new IllegalArgumentException("No such broker " + brokerId);
        }

        monitoringService.reconnect(client, broker);
    }

    @Command
    public void env(Context ctx) {
        Map<String, Object> properties = getAllProperties();

        List<String> keys = new ArrayList<>(properties.keySet());
        keys.removeIf(key -> !key.startsWith("emma."));
        keys.sort(String::compareTo);

        for (String key : keys) {
            ctx.out().printf("%-30s %s%n", key, properties.get(key));
        }
        ctx.out().flush();
    }

    @Command
    public void pingreq(Context ctx, String source, String target) {
        monitoringService.pingRequest(
                NetUtils.parseSocketAddress(source),
                NetUtils.parseSocketAddress(target)
        );
    }

    private Map<String, Object> getAllProperties() {
        Map<String, Object> map = new HashMap<>();
        for (PropertySource<?> propertySource : ((AbstractEnvironment) environment).getPropertySources()) {
            if (propertySource instanceof MapPropertySource) {
                map.putAll(((MapPropertySource) propertySource).getSource());
            }
        }
        return map;
    }

}
