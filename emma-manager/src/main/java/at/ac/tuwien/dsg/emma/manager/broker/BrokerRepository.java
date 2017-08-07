package at.ac.tuwien.dsg.emma.manager.broker;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class BrokerRepository {

    private Map<String, BrokerInfo> brokers;

    public BrokerRepository() {
        this.brokers = new HashMap<>();
    }

    public BrokerInfo getBrokerInfo(String id) {
        return brokers.get(id);
    }

    public BrokerInfo getBrokerInfo(String host, int port) {
        return getBrokerInfo(id(host, port));
    }

    public void register(String host, int port) {
        String id = id(host, port);

        BrokerInfo brokerInfo = new BrokerInfo(host, port);
        getBrokers().put(id, brokerInfo);
    }

    public BrokerInfo remove(String host, int port) {
        BrokerInfo brokerInfo = getBrokerInfo(host, port);
        if (brokerInfo != null) {
            brokers.remove(id(host, port));
        }
        return brokerInfo;
    }

    public Map<String, BrokerInfo> getBrokers() {
        return brokers;
    }

    private String id(String host, int port) {
        return host + ":" + port;
    }

}
