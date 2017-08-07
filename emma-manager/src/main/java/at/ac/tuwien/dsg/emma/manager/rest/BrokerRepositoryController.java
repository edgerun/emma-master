package at.ac.tuwien.dsg.emma.manager.rest;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.broker.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.broker.BrokerRepository;

/**
 * BrokerRepositoryController.
 */
@RestController
public class BrokerRepositoryController {

    @Autowired
    private BrokerRepository brokerRepository;

    @RequestMapping(value = "/broker/register", method = RequestMethod.GET)
    public @ResponseBody
    void register(
            @RequestParam(required = false) String address,
            @RequestParam int port,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (address == null) {
            address = request.getRemoteAddr();
        }

        if (brokerRepository.getBrokerInfo(address, port) != null) {
            response.sendError(409, "broker exists");
            return;
        }

        brokerRepository.register(address, port);
        response.setStatus(201);
    }

    @RequestMapping(value = "/broker/deregister", method = RequestMethod.GET)
    public @ResponseBody
    void deregister(
            @RequestParam(required = false) String address,
            @RequestParam int port,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        if (address == null) {
            address = request.getRemoteAddr();
        }

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
    }


    @RequestMapping(value = "/broker/list")
    public Collection<BrokerInfo> list() {
        return brokerRepository.getBrokers().values();
    }

}
