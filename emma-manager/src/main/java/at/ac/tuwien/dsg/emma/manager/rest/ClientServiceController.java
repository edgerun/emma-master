package at.ac.tuwien.dsg.emma.manager.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import at.ac.tuwien.dsg.emma.manager.broker.BrokerInfo;
import at.ac.tuwien.dsg.emma.manager.broker.BrokerRepository;

@RestController
public class ClientServiceController {

    @Autowired
    private BrokerRepository brokerRepository;

    @RequestMapping(value = "/client/connect", method = RequestMethod.GET)
    public @ResponseBody
    String connect(HttpServletRequest request) {

        // TODO broker selection mechanism
        for (BrokerInfo brokerInfo : brokerRepository.getBrokers().values()) {
            return uri(brokerInfo);
        }

        return getRootBroker();
    }

    private String uri(BrokerInfo brokerInfo) {
        return "tcp://" + brokerInfo.getAddress() + ":" + brokerInfo.getPort();
    }

    public String getRootBroker() {
        // TODO
        return "tcp://localhost:1883";
    }
}
