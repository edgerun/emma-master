package at.ac.tuwien.dsg.emma.manager.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.network.Broker;
import at.ac.tuwien.dsg.emma.manager.network.BrokerRepository;

@RestController
public class ClientServiceController {

    @Autowired
    private BrokerRepository brokerRepository;

    @RequestMapping(value = "/client/connect", method = RequestMethod.GET)
    public @ResponseBody
    String connect(HttpServletRequest request) {

        // TODO broker selection mechanism
        for (Broker brokerInfo : brokerRepository.getBrokers().values()) {
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
