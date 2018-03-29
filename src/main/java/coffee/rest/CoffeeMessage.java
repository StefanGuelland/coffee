package coffee.rest;

import coffee.CoffeeStateMachineService;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import json.InstantTimeSerializer;
import json.LocalDateTimeDeserializer;
import lombok.Data;

import java.time.Instant;

@Data
public class CoffeeMessage {
    private final CoffeeStateMachineService.States state;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = InstantTimeSerializer.class)
    private final Instant time;
}
