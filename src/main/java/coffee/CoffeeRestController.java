package coffee;

import coffee.rest.CoffeeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CoffeeRestController {

    @Autowired
    private CoffeeStateMachineService stateMachnine;

    @RequestMapping("/api/rest/coffee")
    public CoffeeMessage CoffeeMessage() {
        return stateMachnine.CoffeeMessage();
    }

}
