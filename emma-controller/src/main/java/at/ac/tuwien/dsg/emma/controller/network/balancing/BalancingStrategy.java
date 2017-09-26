package at.ac.tuwien.dsg.emma.controller.network.balancing;

import java.util.List;

import at.ac.tuwien.dsg.emma.controller.network.Network;

/**
 * BalancingStrategy.
 */
public interface BalancingStrategy {
    List<BalancingOperation> balance(Network network);
}
