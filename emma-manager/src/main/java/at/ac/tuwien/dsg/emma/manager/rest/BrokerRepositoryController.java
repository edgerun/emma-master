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

import at.ac.tuwien.dsg.emma.manager.broker.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.broker.BrokerRepository;
import at.ac.tuwien.dsg.emma.manager.ec.MonitoringService;
import at.ac.tuwien.dsg.emma.manager.ec.NetworkManager;

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

        if (brokerRepository.getBrokerInfo(address, port) != null) {
            response.sendError(409, "broker exists");
            return;
        }

        BrokerInfo registered = brokerRepository.register(address, port);
        response.setStatus(201);
        networkManager.add(registered);

        for (BrokerInfo brokerInfo : brokerRepository.getBrokers().values()) {
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

        if (brokerRepository.getBrokerInfo(address, port) == null) {
            response.sendError(409, "broker doesn't exist");
            return;
        }

        BrokerInfo brokerInfo = brokerRepository.remove(address, port);
        if (brokerInfo == null) {
            response.setStatus(200);
        } else {
            brokerInfo.setAlive(false);
            response.setStatus(201);
        }

        networkManager.remove(brokerInfo);
    }


    @RequestMapping(value = "/broker/list")
    public Collection<BrokerInfo> list() {
        LOG.debug("/broker/list");
        return brokerRepository.getBrokers().values();
    }

}
