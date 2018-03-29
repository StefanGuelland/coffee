package coffee;

import coffee.mqtt.CoffeeMqttEnergy;
import coffee.mqtt.PalaverLampe;
import coffee.rest.CoffeeMessage;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@Service
public class CoffeeStateMachineService {
    public enum States {
        init,
        working,
        ready,
    }

    public CoffeeStateMachineService() {
        this.coffeeAge = Instant.now();
    }


    @Autowired
    private PalaverLampe palaverLampe;

    @Getter
    private States state = States.init;

    public void setState(States state) {
        if(state == this.state)
            return;
        this.state = state;
        this.coffeeAge = Instant.now();
        try {
            sendCoffeeMessage(CoffeeMessage());
        }
        catch (Exception ex) {

        }

        if(States.ready == state)
            palaverLampe.TriggerPalaver();
    }

    @Getter
    private Instant coffeeAge;

    public CoffeeMessage CoffeeMessage() {
        return new CoffeeMessage(
                this.getState(),
                this.getCoffeeAge());
    }

    // Websocket

    @Autowired
    private SimpMessagingTemplate template;

    public void sendCoffeeMessage(CoffeeMessage message) throws Exception {
        this.template.convertAndSend("/coffee/state", message);
    }

    @SubscribeMapping("/coffee/state")
    public CoffeeMessage handle() {
        return new CoffeeMessage(this.state, this.coffeeAge);
    }

//    @MessageMapping("/coffee/state")
//    public CoffeeMessage handle(CoffeeMessage message) {
//        return message;
//    }


    // MQTT
    public void IncomingMqttMessage(CoffeeMqttEnergy mqttEnergy) {
        switch (getState()) {
            case init:
            case ready:
                if (mqttEnergy.getPower() > 500)
                    setState(States.working);
                break;
            case working:
                if (mqttEnergy.getPower() < 500)
                    setState(States.ready);
                break;
            default:
                break;

        }
    }
}
