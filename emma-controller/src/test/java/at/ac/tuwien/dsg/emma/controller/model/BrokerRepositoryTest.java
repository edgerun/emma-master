package at.ac.tuwien.dsg.emma.controller.model;

/**
 * BrokerRepositoryTest.
 */
public class BrokerRepositoryTest extends AbstractHostRepositoryTest<Broker, BrokerRepository> {
    @Override
    protected BrokerRepository createRepository() {
        return new BrokerRepository();
    }
}
