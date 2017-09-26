package at.ac.tuwien.dsg.emma.controller.model;

import org.springframework.stereotype.Repository;

/**
 * ClientRepository.
 */
@Repository
public class ClientRepository extends HostRepository<Client> {
    @Override
    protected Client createHostObject(String host, int port) {
        return new Client(host, port);
    }
}
