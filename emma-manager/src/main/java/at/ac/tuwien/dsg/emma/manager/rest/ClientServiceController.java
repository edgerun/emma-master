package at.ac.tuwien.dsg.emma.manager.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientServiceController {

    @RequestMapping("/client/connect")
    public ConnectResponse connect() {
        return new ConnectResponse("tcp://my.real.broker:1883");
    }
}
