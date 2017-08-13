package at.ac.tuwien.dsg.emma.manager.network;

/**
 * ClientRepositoryTest.
 */
public class ClientRepositoryTest extends AbstractHostRepositoryTest<Client, ClientRepository> {
    @Override
    protected ClientRepository createRepository() {
        return new ClientRepository();
    }
}
