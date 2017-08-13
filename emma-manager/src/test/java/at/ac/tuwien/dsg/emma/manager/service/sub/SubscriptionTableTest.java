package at.ac.tuwien.dsg.emma.manager.service.sub;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.model.Broker;

/**
 * SubscriptionTableTest.
 */
public class SubscriptionTableTest {

    private Broker broker1;
    private Broker broker2;
    private String filter1;
    private String filter2;
    private SubscriptionTable table;

    @Before
    public void setUp() throws Exception {
        broker1 = new Broker("b1", 1);
        broker2 = new Broker("b2", 2);
        filter1 = "f1";
        filter2 = "f2";

        table = new SubscriptionTable();
    }

    @Test
    public void get_onNonExistingEntry_returnsNull() throws Exception {
        table.getOrCreate(broker1, filter1);

        assertNull(table.get(broker1, filter2));
        assertNull(table.get(broker2, filter1));
    }

    @Test
    public void get_onExistingEntry_returnsCorrectEntry() throws Exception {
        Subscription sub = table.getOrCreate(broker1, filter1);

        assertThat(table.get(broker1, filter1), is(sub));
    }

    @Test
    public void getOrCreate_createsEntryLazily() throws Exception {
        Subscription sub = table.getOrCreate(broker1, filter1);

        assertThat(sub.getBroker(), is(broker1));
        assertThat(sub.getFilter(), is(filter1));

        assertThat(table.getSubscriptions().size(), is(1));
    }

    @Test
    public void getOrCreate_updatesIndices() throws Exception {
        Subscription sub = table.getOrCreate(broker1, filter1);

        assertThat(table.getSubscriptions(broker1).size(), is(1));
        assertThat(table.getSubscriptions(broker1).iterator().next(), is(sub));
        assertThat(table.getSubscriptions(broker2).size(), is(0));

        assertThat(table.getSubscriptions(filter1).size(), is(1));
        assertThat(table.getSubscriptions(filter1).iterator().next(), is(sub));
        assertThat(table.getSubscriptions(filter2).size(), is(0));
    }

    @Test
    public void getOrCreate_onExistingEntry_returnsSameInstance() throws Exception {
        Subscription sub1 = table.getOrCreate(broker1, filter1);
        Subscription sub2 = table.getOrCreate(broker1, filter1);

        assertTrue(sub1 == sub2);
    }

    @Test
    public void remove_removesEntryAndUpdatesIndices() throws Exception {
        Subscription sub = table.getOrCreate(broker1, filter1);

        table.remove(sub);

        assertThat(table.getSubscriptions().size(), is(0));
        assertThat(table.getSubscriptions(broker1).size(), is(0));
        assertThat(table.getSubscriptions(filter1).size(), is(0));
    }
}