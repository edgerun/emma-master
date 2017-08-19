package at.ac.tuwien.dsg.emma.manager.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.bridge.BridgingTableEntry;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisBridgingTableIT {

    private JedisPool jedis;

    @Before
    public void setUp() throws Exception {
        this.jedis = new JedisPool();
    }

    @After
    public void tearDown() throws Exception {
        try (Jedis resource = jedis.getResource()) {
            resource.flushAll();
        }
        jedis.close();
    }

    @Test
    public void insertAndGetAll() throws Exception {
        RedisBridgingTable table = new RedisBridgingTable(jedis);

        table.insert(new BridgingTableEntry("t1", "b1", "b2"));
        table.insert(new BridgingTableEntry("t1", "b2", "b3"));
        table.insert(new BridgingTableEntry("t2", "b2", "b3"));

        Collection<BridgingTableEntry> entries = table.getAll();

        assertThat(entries.size(), is(3));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b1", "b2")));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
    }

    @Test
    public void insertCollection() throws Exception {
        RedisBridgingTable table = new RedisBridgingTable(jedis);

        table.insert(new BridgingTableEntry("t1", "b1", "b2"));
        table.insert(Arrays.asList(new BridgingTableEntry("t1", "b2", "b3"), new BridgingTableEntry("t2", "b2", "b3")));

        Collection<BridgingTableEntry> entries = table.getAll();

        assertThat(entries.size(), is(3));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b1", "b2")));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
    }

    @Test
    public void delete_removesEntryCorrectly() throws Exception {
        RedisBridgingTable table = new RedisBridgingTable(jedis);

        table.insert(new BridgingTableEntry("t1", "b1", "b2"));
        table.insert(new BridgingTableEntry("t1", "b2", "b3"));
        table.insert(new BridgingTableEntry("t2", "b2", "b3"));

        table.delete(new BridgingTableEntry("t1", "b2", "b3"));

        Collection<BridgingTableEntry> entries = table.getAll();

        assertThat(entries.size(), is(2));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b1", "b2")));
        assertThat(entries, hasItem(new BridgingTableEntry("t2", "b2", "b3")));
    }

    @Test
    public void deleteBridge_removesCorrectEntries() throws Exception {
        RedisBridgingTable table = new RedisBridgingTable(jedis);

        table.insert(new BridgingTableEntry("t1", "b1", "b2")); // contains b2 as destination
        table.insert(new BridgingTableEntry("t1", "b2", "b3")); // contains b2 as source
        table.insert(new BridgingTableEntry("t2", "b1", "b3")); // does not contain b2

        table.deleteBridge("b2");

        Collection<BridgingTableEntry> entries = table.getAll();

        assertThat(entries.size(), is(1));
        assertThat(entries, hasItem(new BridgingTableEntry("t2", "b1", "b3")));
    }

    @Test
    public void getForSource_returnsCorrectEntries() throws Exception {
        RedisBridgingTable table = new RedisBridgingTable(jedis);

        table.insert(new BridgingTableEntry("t1", "b1", "b2"));
        table.insert(new BridgingTableEntry("t1", "b2", "b3"));
        table.insert(new BridgingTableEntry("t2", "b2", "b3"));

        Collection<BridgingTableEntry> entries;

        entries = table.getForSource("b1");
        assertThat(entries.size(), is(1));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b1", "b2")));

        entries = table.getForSource("b2");
        assertThat(entries.size(), is(2));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
        assertThat(entries, hasItem(new BridgingTableEntry("t1", "b2", "b3")));
    }
}