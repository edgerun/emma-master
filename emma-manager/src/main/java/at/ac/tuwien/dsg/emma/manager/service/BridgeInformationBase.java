package at.ac.tuwien.dsg.emma.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import redis.clients.jedis.JedisPool;

/**
 * BrokerInformationBase.
 */
@Service
public class BridgeInformationBase {

    private JedisPool jedis;

    @Autowired
    public BridgeInformationBase(JedisPool jedis) {
        this.jedis = jedis;
    }

    public void add(Broker broker) {
        jedis.getResource().sadd("emma.brokers", broker.getId());
    }

    public void remove(Broker broker) {
        jedis.getResource().srem("emma.brokers", broker.getId());
    }

}
