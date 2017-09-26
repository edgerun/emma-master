package at.ac.tuwien.dsg.emma.controller.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.NodeInfo;
import at.ac.tuwien.dsg.emma.controller.event.BrokerDisconnectEvent;
import at.ac.tuwien.dsg.emma.controller.model.Broker;
import at.ac.tuwien.dsg.emma.controller.model.BrokerRepository;

/**
 * BrokerRepositoryController.
 */
@RestController
public class BrokerRepositoryController {

    private static final Logger LOG = LoggerFactory.getLogger(BrokerRepositoryController.class);

    @Autowired
    private BrokerRepository brokerRepository;

    @Autowired
    private ApplicationEventPublisher systemEvents;

    @RequestMapping(value = "/broker/register", method = RequestMethod.GET)
    public String register(NodeInfo info, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.debug("/broker/register({})", info);

        if (info.getPort() == 0) {
            response.sendError(400, "No port specified");
            return "";
        }
        if (info.getMonitoringPort() == 0) {
            response.sendError(400, "No monitoring port specified");
            return "";
        }
        if (info.isHostWildcard()) {
            LOG.debug("Host is a wildcard, using remote address of request {}", request.getRemoteAddr());
            info.setHost(request.getRemoteAddr());
        }

        if (brokerRepository.getHost(info.getHost(), info.getPort()) != null) {
            LOG.debug("Broker already registered");
            response.sendError(409, "host " + info.getHost() + ":" + info.getPort() + " exists");
            return "";
        }

        LOG.info("Registering new broker {}", info);

        Broker registered = brokerRepository.register(info);
        response.setStatus(201);
        return registered.getId();
    }

    @RequestMapping(value = "/broker/deregister", method = RequestMethod.GET)
    public @ResponseBody
    void deregister(String id, HttpServletResponse response) throws IOException {
        LOG.debug("/broker/deregister({})", id);

        Broker broker = brokerRepository.getById(id);

        if (broker == null) {
            response.sendError(409, "broker doesn't exist");
            return;
        }

        boolean removed = brokerRepository.remove(broker);
        if (removed) {
            broker.setAlive(false);
            response.setStatus(201);

            systemEvents.publishEvent(new BrokerDisconnectEvent(broker));
        }
    }

    @RequestMapping(value = "/broker/list")
    public Collection<Broker> list() {
        LOG.debug("/broker/list");
        return brokerRepository.getHosts().values();
    }

}
