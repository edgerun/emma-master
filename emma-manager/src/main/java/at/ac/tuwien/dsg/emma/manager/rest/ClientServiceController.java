package at.ac.tuwien.dsg.emma.manager.rest;

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
    String connect(String gatewayId, HttpServletResponse response) {
        LOG.debug("/client/connect({})", gatewayId);

        Client client = clientRepository.getById(gatewayId);

        if (client == null) {
            LOG.debug("Client {} not known, registering", gatewayId);
            client = clientRepository.register(gatewayId);
            networkManager.add(client);
        }

        try {
            Broker broker = brokerSelectionStrategy.select(client, networkManager.getNetwork());

            if (broker == null) {
                throw new IllegalStateException("No broker in networks");
            }

            LOG.info("Selected broker for client {}: {}", client, broker);
            return uri(broker);
        } catch (IllegalStateException e) {
            LOG.warn("No broker connected {}", e.getMessage());
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
            LOG.debug("Client {} not known, registering", gatewayId);
            return;
        }
        networkManager.remove(client);
    }

    private String uri(Broker brokerInfo) {
        return "tcp://" + brokerInfo.getHost() + ":" + brokerInfo.getPort();
    }

}
