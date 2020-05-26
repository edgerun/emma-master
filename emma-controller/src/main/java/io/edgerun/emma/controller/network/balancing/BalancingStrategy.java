package io.edgerun.emma.controller.network.balancing;

import java.util.List;

import io.edgerun.emma.controller.network.Network;

/**
 * BalancingStrategy.
 */
public interface BalancingStrategy {
    List<BalancingOperation> balance(Network network);
}
