package at.ac.tuwien.dsg.emma.manager.model;

/**
 * BrokerRepositoryTest.
 */
public class BrokerRepositoryTest extends AbstractHostRepositoryTest<Broker, BrokerRepository> {
    @Override
    protected BrokerRepository createRepository() {
        return new BrokerRepository();
    }
}
