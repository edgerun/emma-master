package at.ac.tuwien.dsg.emma.manager.model;

import java.util.HashMap;
import java.util.Map;

/**
 * HostRepository.
 */
public abstract class HostRepository<H extends Host> {

    private Map<String, H> hosts;

    public HostRepository() {
        this.hosts = new HashMap<>();
    }

    public H getById(String id) {
        return hosts.get(id);
    }

    public H getHost(String host, int port) {
        return getById(id(host, port));
    }

    public H register(String host, int port) {
        String id = id(host, port);

        H brokerInfo = createHostObject(host, port);
        getHosts().put(id, brokerInfo);
        return brokerInfo;
    }

    public H register(String id) {
        H brokerInfo = createHostObject(id);
        getHosts().put(id, brokerInfo);
        return brokerInfo;
    }

    public H remove(String host, int port) {
        H brokerInfo = getHost(host, port);
        if (brokerInfo != null) {
            hosts.remove(id(host, port));
        }
        return brokerInfo;
    }

    public Map<String, H> getHosts() {
        return hosts;
    }

    private String id(String host, int port) {
        return host + ":" + port;
    }

    protected abstract H createHostObject(String host, int port);

    protected H createHostObject(String id) {
        String[] parts = id.split(":");
        return createHostObject(parts[0], Integer.parseInt(parts[1]));
    }

}
