package at.ac.tuwien.dsg.emma.manager.network.balancing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.Host;
import at.ac.tuwien.dsg.emma.manager.network.Link;
import at.ac.tuwien.dsg.emma.manager.network.Network;
import at.ac.tuwien.dsg.emma.manager.network.graph.Edge;
import at.ac.tuwien.dsg.emma.manager.network.graph.Node;
import at.ac.tuwien.dsg.emma.util.MultiValueHashMap;
import at.ac.tuwien.dsg.emma.util.MultiValueMap;

/**
 * ConnectionBalancingStrategyTest.
 */
@Ignore
public class ConnectionBalancingStrategyTest {
    ConnectionBalancingStrategy strategy = new ConnectionBalancingStrategy();

    Node<Broker> nb1;
    Node<Broker> nb2;
    Node<Broker> nb3;
    Node<Broker> nb4;

    List<Node<Client>> clientNodes;

    Network graph;

    @Before
    public void setUp() throws Exception {
        graph = new Network();

        nb1 = new Node<>("1.0.0.0:1", new Broker("1.0.0.0", 1));
        nb2 = new Node<>("1.0.0.0:2", new Broker("1.0.0.0", 2));
        nb3 = new Node<>("1.0.0.0:3", new Broker("1.0.0.0", 3));
        nb4 = new Node<>("1.0.0.0:4", new Broker("1.0.0.0", 4));

        nb1.getValue().setAlive(true);
        nb2.getValue().setAlive(true);
        nb3.getValue().setAlive(true);
        nb4.getValue().setAlive(true);

        graph.addNode(nb1);
        graph.addNode(nb2);
        graph.addNode(nb3);
        graph.addNode(nb4);

        clientNodes = generateClientNodes(20);

        for (Node<Client> cn : clientNodes) {
            graph.addNode(cn);

            Edge<Host, Link> en1 = graph.addEdge(cn, nb1);
            Edge<Host, Link> en2 = graph.addEdge(cn, nb2);
            Edge<Host, Link> en3 = graph.addEdge(cn, nb3);
            Edge<Host, Link> en4 = graph.addEdge(cn, nb4);

            en1.setValue(new Link().addLatency(1));
            en2.setValue(new Link().addLatency(1).addLatency(2));
            en3.setValue(new Link().addLatency(2).addLatency(1));
            en4.setValue(new Link().addLatency(30));
        }

    }

    @Test
    public void rebalance_fromHigherLevel() throws Exception {
        for (int i = 0; i < clientNodes.size(); i++) {
            Node<Client> node = clientNodes.get(i);
            node.getValue().setConnectedTo(nb4.getValue());
        }

        debug();
        System.out.println("->");
        doBalancing();
        debug();
    }

    @Test
    public void rebalance_fromSameLevel() throws Exception {
        for (int i = 0; i < clientNodes.size(); i++) {
            Node<Client> node = clientNodes.get(i);
            node.getValue().setConnectedTo(nb1.getValue());
        }

        debug();
        System.out.println("->");
        doBalancing();
        debug();
        System.out.println("->");
        doBalancing();
        debug();
        System.out.println("->");
        long then = System.currentTimeMillis();
        doBalancing(); // becomes stable
        System.out.printf("took %d ms%n", System.currentTimeMillis() - then);
        debug();
    }

    private void doBalancing() {
        List<BalancingOperation> operations = strategy.balance(graph);

        for (BalancingOperation operation : operations) {
            operation.getClient().setConnectedTo(operation.getTarget());
        }

    }

    private void debug() {
        System.out.println("----");
        MultiValueMap<Broker, Client> connectionMap = getConnectionMap();

        for (Map.Entry<Broker, List<Client>> kv : connectionMap.entrySet()) {
            System.out.printf("%s: %d%n", kv.getKey().getId(), kv.getValue().size());
            for (Client client : kv.getValue()) {
                System.out.printf("           %s%n", client.getId());
            }
        }
        System.out.println("----");
    }

    public MultiValueMap<Broker, Client> getConnectionMap() {
        MultiValueMap<Broker, Client> connectionMap = new MultiValueHashMap<>();

        Collection<Node<Client>> clients = graph.getClientNodes();
        for (Node<Client> client : clients) {
            Broker broker = client.getValue().getConnectedTo();
            if (broker != null) {
                connectionMap.addValue(broker, client.getValue());
            }
        }

        return connectionMap;
    }

    public List<Node<Client>> generateClientNodes(int n) {
        List<Node<Client>> nodes = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            Client client = new Client("0.0.0.0", i);
            nodes.add(i, new Node<>(client.getId(), client));
        }

        return nodes;
    }

}