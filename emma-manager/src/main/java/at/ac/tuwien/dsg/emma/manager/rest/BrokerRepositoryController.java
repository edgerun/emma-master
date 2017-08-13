package at.ac.tuwien.dsg.emma.manager.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.model.Broker;
import at.ac.tuwien.dsg.emma.manager.model.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.network.NetworkManager;
import at.ac.tuwien.dsg.emma.manager.service.MonitoringService;

/**
 * BrokerRepositoryController.
 */
@RestController
public class BrokerRepositoryController {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerRepositoryController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private MonitoringService monitoringService;

    @Autowired
    private NetworkManager networkManager;

    @RequestMapping(value = "/broker/register", method = RequestMethod.GET)
    public @ResponseBody
    void register(
            @RequestParam String address,
            @RequestParam int port,
            HttpServletResponse response) throws IOException {

        LOG.debug("/broker/register({}, {})", address, port);

        if (brokerRepository.getHost(address, port) != null) {
            response.sendError(409, "broker exists");
            return;
        }

        Broker registered = brokerRepository.register(address, port);
        response.setStatus(201);
        networkManager.add(registered);

        for (Broker brokerInfo : brokerRepository.getHosts().values()) {
            // TODO: this is questionable
            if (brokerInfo == registered) {
                continue;
            }

            monitoringService.pingRequest(registered.getHost(), brokerInfo.getHost());
        }

    }

    @RequestMapping(value = "/broker/deregister", method = RequestMethod.GET)
    public @ResponseBody
    void deregister(
            @RequestParam String address,
            @RequestParam int port,
            HttpServletResponse response) throws IOException {

        LOG.debug("/broker/deregister({}, {})", address, port);

        Broker broker = brokerRepository.getHost(address, port);

        if (broker == null) {
            response.sendError(409, "broker doesn't exist");
            return;
        }

        boolean removed = brokerRepository.remove(address, port);
        if (removed) {
            broker.setAlive(false);
            response.setStatus(201);
        }

        networkManager.remove(broker);
    }


    @RequestMapping(value = "/broker/list")
    public Collection<Broker> list() {
        LOG.debug("/broker/list");
        return brokerRepository.getHosts().values();
    }

}
