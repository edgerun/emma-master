package io.edgerun.emma.controller.network.sel;

import io.edgerun.emma.controller.model.Broker;
import io.edgerun.emma.controller.model.Client;
import io.edgerun.emma.controller.network.Network;

/**
 * Strategy for selecting a broker a given client should connect to.
 */
public interface BrokerSelectionStrategy {
    Broker select(Client client, Network network);
}
