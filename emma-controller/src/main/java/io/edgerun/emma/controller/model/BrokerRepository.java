package io.edgerun.emma.controller.model;

import org.springframework.stereotype.Repository;

import io.edgerun.emma.NodeInfo;

@Repository
public class BrokerRepository extends HostRepository<Broker> {

    @Override
    public Broker register(NodeInfo info) {
        Broker broker = super.register(info);
        broker.setMonitoringPort(info.getMonitoringPort());
        return broker;
    }

    @Override
    protected Broker createHostObject(String host, int port) {
        return new Broker(host, port);
    }
}
