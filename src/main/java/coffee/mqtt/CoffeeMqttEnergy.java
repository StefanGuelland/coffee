package coffee.mqtt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import json.LocalDateTimeDeserializer;
import lombok.Data;

@Data
public class CoffeeMqttEnergy {
//    private final Instant time;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private java.time.LocalDateTime time;
    private final Float total;
    private final Float yesterday;
    private final Float today;
    private final Float period;
    private final Float power;
    private final int factor;
    private final Float voltage;
    private final Float current;
}
