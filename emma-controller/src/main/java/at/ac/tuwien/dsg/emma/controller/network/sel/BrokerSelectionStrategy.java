package at.ac.tuwien.dsg.emma.controller.network.sel;

import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.network.Network;

/**
 * Strategy for selecting a broker a given client should connect to.
 */
public interface BrokerSelectionStrategy {
    Broker select(Client client, Network network);
}
