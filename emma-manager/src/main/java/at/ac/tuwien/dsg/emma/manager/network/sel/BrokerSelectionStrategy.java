package at.ac.tuwien.dsg.emma.manager.network.sel;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.network.Network;

/**
 * Strategy for selecting a broker a given client should connect to.
 */
public interface BrokerSelectionStrategy {
    Broker select(Client client, Network network);
}
