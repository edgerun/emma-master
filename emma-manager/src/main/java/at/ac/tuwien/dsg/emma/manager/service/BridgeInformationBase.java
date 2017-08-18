package at.ac.tuwien.dsg.emma.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
