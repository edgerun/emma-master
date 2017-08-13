package at.ac.tuwien.dsg.emma.manager.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.ClientRepository;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.network.sel.BrokerSelectionStrategy;

@RestController
public class ClientServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceController.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BrokerSelectionStrategy brokerSelectionStrategy;

    @Autowired
    private NetworkManager networkManager;

    @RequestMapping(value = "/client/connect", method = RequestMethod.GET)
    public @ResponseBody
    void connect(String gatewayId, HttpServletResponse response) throws IOException {
        LOG.debug("/client/connect({})", gatewayId);

        Client client = clientRepository.getById(gatewayId);

        if (client != null) {
            response.sendError(409, "client connected");
            return;
        }

        client = clientRepository.register(gatewayId);
        networkManager.add(client);
        LOG.info("Registered client {}", client);
    }

    @RequestMapping(value = "/client/broker", method = RequestMethod.GET)
    public @ResponseBody
    String getBroker(String gatewayId, HttpServletResponse response) throws IOException {
        LOG.debug("/client/broker({})", gatewayId);

        Client client = clientRepository.getById(gatewayId);
        if (client == null) {
            response.sendError(428, "register client first");
            return null;
        }

        try {
            Broker broker = brokerSelectionStrategy.select(client, networkManager.getNetwork());

            if (broker == null) {
                throw new IllegalStateException("No broker in networks");
            }

            LOG.info("Selected broker for client {}: {}", client, broker);
            return uri(broker);
        } catch (IllegalStateException e) {
            LOG.info("No broker connected {}", e.getMessage());
            response.setStatus(503);
            return "";
        }
    }

    @RequestMapping(value = "/client/disconnect", method = RequestMethod.GET)
    public @ResponseBody
    void disconnect(String gatewayId, HttpServletResponse response) {
        LOG.debug("/client/disconnect({})", gatewayId);

        Client client = clientRepository.getById(gatewayId);
        if (client == null) {
            return;
        }

        if (clientRepository.remove(client)) {
            response.setStatus(201);
        }
        networkManager.remove(client);
    }

    private String uri(Broker brokerInfo) {
        return "tcp://" + brokerInfo.getHost() + ":" + brokerInfo.getPort();
    }

}
