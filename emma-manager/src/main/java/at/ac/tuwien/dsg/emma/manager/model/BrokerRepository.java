package at.ac.tuwien.dsg.emma.manager.model;

import org.springframework.stereotype.Repository;

@Repository
public class BrokerRepository extends HostRepository<Broker> {

    @Override
    protected Broker createHostObject(String host, int port) {
        return new Broker(host, port);
    }
}
