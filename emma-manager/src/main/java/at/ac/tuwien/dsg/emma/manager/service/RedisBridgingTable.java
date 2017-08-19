package at.ac.tuwien.dsg.emma.manager.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.dsg.emma.bridge.BridgingTable;
import at.ac.tuwien.dsg.emma.bridge.BridgingTableEntry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

/**
 * Maintains a BridgingTable directly via Redis.
 */
public class RedisBridgingTable implements BridgingTable {

    private static final String KEY_PREFIX = "emma.bt.";

    private JedisPool jedisPool;

    public RedisBridgingTable(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void insert(BridgingTableEntry entry) {
        insert(Collections.singleton(entry));
    }

    @Override
    public void insert(Collection<BridgingTableEntry> entries) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();

            for (BridgingTableEntry entry : entries) {
                insert(pipeline, entry);
            }

            pipeline.sync();
        }
    }

    @Override
    public void delete(BridgingTableEntry entry) {
        delete(Collections.singleton(entry));
    }

    @Override
    public void delete(Collection<BridgingTableEntry> entries) {
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            entries.forEach(e -> delete(pipeline, e));
            pipeline.sync();
        }
    }

    @Override
    public void deleteBridge(String id) {
        // FIXME efficiency, atomicity
        Collection<BridgingTableEntry> entries = getAll();

        entries.removeIf(e -> !id.equals(e.getSource()) && !id.equals(e.getDestination()));

        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            entries.forEach(e -> delete(pipeline, e));
            pipeline.srem("emma.bridges", id);
            pipeline.sync();
        }
    }

    @Override
    public Collection<BridgingTableEntry> getAll() {
        Set<BridgingTableEntry> result = new HashSet<>();

        try (Jedis jedis = jedisPool.getResource()) {
            for (String bridge : jedis.smembers("emma.bridges")) {
                String topicsKey = KEY_PREFIX + bridge;
                for (String topic : jedis.smembers(topicsKey)) {
                    String destinationsKey = KEY_PREFIX + bridge + "." + topic;
                    for (String destination : jedis.smembers(destinationsKey)) {
                        result.add(new BridgingTableEntry(topic, bridge, destination));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Collection<BridgingTableEntry> getForSource(String bridge) {
        Set<BridgingTableEntry> result = new HashSet<>();

        try (Jedis jedis = jedisPool.getResource()) {
            String topicsKey = KEY_PREFIX + bridge;
            for (String topic : jedis.smembers(topicsKey)) {
                String destinationsKey = KEY_PREFIX + bridge + "." + topic;
                for (String destination : jedis.smembers(destinationsKey)) {
                    result.add(new BridgingTableEntry(topic, bridge, destination));
                }
            }
        }

        return result;
    }

    private void insert(Pipeline pipeline, BridgingTableEntry entry) {
        String topicsKey = KEY_PREFIX + entry.getSource();
        String routeKey = KEY_PREFIX + entry.getSource() + "." + entry.getTopic();

        pipeline.sadd(topicsKey, entry.getTopic());
        pipeline.sadd(routeKey, entry.getDestination());
        pipeline.sadd("emma.bridges", entry.getSource());
    }

    private void delete(Pipeline pipeline, BridgingTableEntry entry) {
        String destinationsKey = KEY_PREFIX + entry.getSource() + "." + entry.getTopic();
        pipeline.srem(destinationsKey, entry.getDestination());
    }
}
