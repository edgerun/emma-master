package io.edgerun.emma.controller.model;

/**
 * ClientRepositoryTest.
 */
public class ClientRepositoryTest extends AbstractHostRepositoryTest<Client, ClientRepository> {
    @Override
    protected ClientRepository createRepository() {
        return new ClientRepository();
    }
}
