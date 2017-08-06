package at.ac.tuwien.dsg.emma.manager.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientServiceController {

    @RequestMapping("/client/connect")
    public String connect() {
        return getRootBroker();
    }

    public String getRootBroker() {
        return "tcp://localhost:1883";
    }
}
