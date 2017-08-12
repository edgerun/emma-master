package at.ac.tuwien.dsg.emma.manager.network.sel;

import at.ac.tuwien.dsg.emma.manager.network.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.network.ClientInfo;
import at.ac.tuwien.dsg.emma.manager.network.HostInfo;
import at.ac.tuwien.dsg.emma.manager.network.Metrics;
import at.ac.tuwien.dsg.emma.manager.network.graph.Graph;

/**
 * Strategy for selecting a broker a given client should connect to.
 */
public interface BrokerSelectionStrategy {
    BrokerInfo select(ClientInfo client, Graph<HostInfo, Metrics> graph);
}
