package at.ac.tuwien.dsg.emma.manager.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.model.Client;
import at.ac.tuwien.dsg.emma.manager.model.ClientRepository;

@RestController
public class ClientServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(ClientServiceController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/client/connect", method = RequestMethod.GET)
    public @ResponseBody
    String connect(String gatewayId, HttpServletRequest request) {

        Client client = clientRepository.getById(gatewayId);

        if (client == null) {
            String host = gatewayId.split(":")[0];
            int port = Integer.parseInt(gatewayId.split(":")[1]);
            client = clientRepository.register(host, port);
        }

        LOG.info("client: {}", client);

        // TODO broker selection mechanism
        for (Broker brokerInfo : brokerRepository.getHosts().values()) {
            return uri(brokerInfo);
        }

        return getRootBroker();
    }

    private String uri(Broker brokerInfo) {
        return "tcp://" + brokerInfo.getHost() + ":" + brokerInfo.getPort();
    }

    public String getRootBroker() {
        // TODO
        return "tcp://localhost:1883";
    }
}
