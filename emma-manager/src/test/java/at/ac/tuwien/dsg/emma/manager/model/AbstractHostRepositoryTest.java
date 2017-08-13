package at.ac.tuwien.dsg.emma.manager.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.model.HostRepository;

/**
 * AbstractHostRepositoryTest.
 */
public abstract class AbstractHostRepositoryTest<H extends Host, R extends HostRepository<H>> {

    protected R repo;

    @Before
    public void setUp() throws Exception {
        repo = createRepository();
    }

    @Test
    public void createHostObject_createsObjectCorrectly() throws Exception {
        H host = repo.createHostObject("10.42.0.1", 1883);

        assertNotNull(host);
        assertEquals("10.42.0.1:1883", host.getId());
        assertEquals("10.42.0.1", host.getHost());
        assertEquals(1883, host.getPort());
    }

    @Test
    public void getById_findsRegisteredObject() throws Exception {
        H host = repo.register("10.42.0.1", 1883);
        H found = repo.getById(host.getId());

        assertEquals(host, found);
    }

    @Test
    public void get_findsRegisteredObject() throws Exception {
        H host = repo.register("10.42.0.1", 1883);
        H found = repo.getHost("10.42.0.1", 1883);

        assertEquals(host, found);
    }

    @Test
    public void remove_removesRegisteredObject() throws Exception {
        H host = repo.register("10.42.0.1", 1883);

        repo.remove(host.getHost(), host.getPort());

        assertNull(repo.getHost("10.42.0.1", 1883));
        assertNull(repo.getById("10.42.0.1:1883"));
    }

    protected abstract R createRepository();

}