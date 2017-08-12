package at.ac.tuwien.dsg.emma.manager.network;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BrokerRepository {

    private Map<String, Broker> brokers;

    public BrokerRepository() {
        this.brokers = new HashMap<>();
    }

    public Broker getBrokerByHost(String host) {
        for (Map.Entry<String, Broker> entry : brokers.entrySet()) {
            if (host.equals(entry.getKey().split(":")[0])) {
                return entry.getValue();
            }
        }

        return null;
    }

    public Broker getBroker(String id) {
        return brokers.get(id);
    }

    public Broker getBroker(String host, int port) {
        return getBroker(id(host, port));
    }

    public Broker register(String host, int port) {
        String id = id(host, port);

        Broker brokerInfo = new Broker(host, port);
        getBrokers().put(id, brokerInfo);
        return brokerInfo;
    }

    public Broker remove(String host, int port) {
        Broker brokerInfo = getBroker(host, port);
        if (brokerInfo != null) {
            brokers.remove(id(host, port));
        }
        return brokerInfo;
    }

    public Map<String, Broker> getBrokers() {
        return brokers;
    }

    private String id(String host, int port) {
        return host + ":" + port;
    }

}
