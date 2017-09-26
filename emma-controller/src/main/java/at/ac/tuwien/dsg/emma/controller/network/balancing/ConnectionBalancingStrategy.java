package at.ac.tuwien.dsg.emma.controller.network.balancing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.Client;
import at.ac.tuwien.dsg.emma.controller.model.Host;
import at.ac.tuwien.dsg.emma.controller.network.Link;
import at.ac.tuwien.dsg.emma.controller.network.Network;
import at.ac.tuwien.dsg.emma.controller.network.graph.Edge;
import at.ac.tuwien.dsg.emma.controller.network.graph.Node;

/**
 * ConnectionBalancingStrategy.
 */
public class ConnectionBalancingStrategy implements BalancingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionBalancingStrategy.class);

    private float migrationThreshold = 0.1f;

    private LatencyGrouping grouping;

    public ConnectionBalancingStrategy() {
        this(new LatencyGrouping());
    }

    public ConnectionBalancingStrategy(LatencyGrouping grouping) {
        this.grouping = grouping;
    }

    @Override
    public List<BalancingOperation> balance(Network network) {
        Collection<Node<Client>> clientNodes = network.getClientNodes();
        List<BalancingOperation> ops = new ArrayList<>(clientNodes.size());

        Map<Broker, Integer> connectionCount = getConnectionMap(network);

        for (Node<Client> clientNode : clientNodes) {
            Collection<Edge<Host, Link>> groups = getCandidates(clientNode, network);

            if (groups.isEmpty()) {
                LOG.warn("No alive candidates in lowest latency group for {}", clientNode);
                continue;
            }

            Client client = clientNode.getValue();
            Broker current = client.getConnectedTo();

            List<Broker> candidates = groups.stream()
                    .map(e -> e.opposite(clientNode))
                    .map(e -> (Broker) e.getValue())
                    .sorted(Comparator.comparingInt(connectionCount::get))
                    .collect(Collectors.toList());

            if (Objects.equals(current, candidates.get(0))) {
                // already connected to the broker with the lowest latency and lowest connect count
                continue;
            }

            Broker candidate = candidates.get(0);

            if (candidates.size() > 1 && candidates.contains(current)) {
                // we know that the client is currently not connected to the best broker

                // first, calculate the threshold
                long totalConnections = 0;
                for (Broker b : candidates) {
                    totalConnections += connectionCount.get(b);
                }
                long minDiff = (long) (totalConnections * migrationThreshold);

                if ((connectionCount.get(candidate) + minDiff) >= connectionCount.get(current)) {
                    // this avoid reconnect a client to a broker that would not balance the network within the
                    // migrationThreshold
                    continue;
                }
            }

            ops.add(new BalancingOperation(client, candidate));
            connectionCount.compute(current, (b, i) -> i == null ? 0 : i - 1);
            connectionCount.compute(candidate, (b, i) -> i + 1);
        }

        return ops;
    }

    public Map<Broker, Integer> getConnectionMap(Network network) {
        Collection<Node<Broker>> brokers = network.getBrokerNodes();
        Map<Broker, Integer> connectionsMap = new HashMap<>(brokers.size());

        for (Node<Broker> brokerNode : brokers) {
            connectionsMap.put(brokerNode.getValue(), 0);
        }

        for (Node<Client> clientNode : network.getClientNodes()) {
            Broker broker = clientNode.getValue().getConnectedTo();
            if (broker != null) {
                connectionsMap.compute(broker, (b, i) -> i + 1);
            }
        }

        return connectionsMap;
    }

    public Collection<Edge<Host, Link>> getCandidates(Node<Client> clientNode, Network network) {
        return grouping.getLowestLatencyGroup(clientNode, network)
                .stream()
                .filter(e -> (((Broker) e.opposite(clientNode).getValue()).isAlive()))
                .collect(Collectors.toSet());
    }

}
