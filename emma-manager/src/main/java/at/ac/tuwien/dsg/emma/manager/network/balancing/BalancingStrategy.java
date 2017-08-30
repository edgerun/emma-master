package at.ac.tuwien.dsg.emma.manager.network.balancing;

import java.util.List;

import at.ac.tuwien.dsg.emma.manager.network.Network;

/**
 * BalancingStrategy.
 */
public interface BalancingStrategy {
    List<BalancingOperation> balance(Network network);
}
