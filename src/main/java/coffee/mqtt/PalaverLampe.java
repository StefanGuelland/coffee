package coffee.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class PalaverLampe {


    public void TriggerPalaver(){
        gateway.sendToMqtt("{\"light\":255, \"motor\": true}");
        TimerTask task = new TimerTask()
        {
            public void run()
            {
                gateway.sendToMqtt("{\"light\":0, \"motor\": false}");

            }
        };
        Timer timer = new Timer();
        timer.schedule(task,30000);
    }

    @Autowired
    public mqtt.MqttClient.MyGateway gateway;

}
