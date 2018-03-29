package mqtt;

import coffee.mqtt.CoffeeMqttEnergy;
import coffee.CoffeeStateMachineService;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

//import main.WebsocketController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageProducer;
//import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;


import org.springframework.integration.channel.DirectChannel;

import org.springframework.integration.mqtt.support.MqttMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

//@EnableIntegration

@IntegrationComponentScan
@Configuration
public class MqttClient {

    @Value("${mqtt.uri}")
    private String mqttServerUri;

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(mqttServerUri);

//        factory.setUserName("guest");
//        factory.setPassword("guest");
        return factory;
    }

    // publisher

//    @Bean
//    public IntegrationFlow mqttOutFlow() {
//        return IntegrationFlows.from(CharacterStreamReadingMessageSource.stdin()
////                e -> e.poller(Pollers.fixedDelay(1000))
//        )
//                .transform(p -> p + " sent to MQTT")
//                .handle(mqttOutbound())
//                .get();
//    }

//    @Bean
//    public MessageHandler mqttOutbound() {
//        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("siSamplePublisher", mqttClientFactory());
//        messageHandler.setAsync(true);
//        messageHandler.setDefaultTopic("siSampleTopic");
//        return messageHandler;
//    }

    // consumer

//    @Bean
//    public IntegrationFlow mqttInFlow() {
//        return IntegrationFlows.from(mqttInbound())
//                .transform(p -> p + ", received from MQTT")
//                .handle(logger())
//                .get();
//    }
//
//    private LoggingHandler logger() {
//        LoggingHandler loggingHandler = new LoggingHandler("INFO");
//        loggingHandler.setLoggerName("siSample");
//        return loggingHandler;
//    }
//
//    @Bean
//    public MessageProducerSupport mqttInbound() {
//        MqttPahoMessageDrivenChannelAdapter adapter =
//                new MqttPahoMessageDrivenChannelAdapter(
//                        "siSampleConsumer",
//                        mqttClientFactory(),
//                        "#");
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(1);
//        return adapter;
//    }


    /////

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        generateClientId(),
                        mqttClientFactory(),
                        "tele/Kaffeemaschine/ENERGY");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Autowired
            private CoffeeStateMachineService coffeeStateMachineService;

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println(Thread.currentThread().getName() + ":" + Thread.currentThread().getId() + ":" + message.getPayload());
                try{
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                    CoffeeMqttEnergy mqttEnergy = mapper.readValue((String) message.getPayload(), CoffeeMqttEnergy.class);
                    coffeeStateMachineService.IncomingMqttMessage(mqttEnergy);

//                    System.out.println(message.getHeaders().get("mqtt_topic", String.class));
//                    System.out.println(mqttEnergy.getTime());
//                    System.out.println(mqttEnergy.getPower());
//                    websocketController.greetingString((String) message.getPayload());
                }
                catch (Exception ex)
                {
                    System.out.println(ex.getMessage());
                }
            }

        };
    }

    //////////////////////////

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(generateClientId(), mqttClientFactory());
//        new MqttPahoMessageHandler("testClient", mqttClientFactory());
        messageHandler.setAsync(true);

        messageHandler.setConverter(new DefaultPahoMessageConverter());
        messageHandler.setDefaultTopic("palaverlampe/broadcast/palaver");
        return messageHandler;
    }

    public static String generateClientId() {
        //length of nanoTime = 15, so total length = 19  < 65535(defined in spec)
        return "coffee" + System.nanoTime();
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

//    @Bean
//    public MqttMessageConverter getMessageConverter() {
//        DefaultPahoMessageConverter messageConverter = new DefaultPahoMessageConverter();
//        messageConverter.setPayloadAsBytes(true);
//        return messageConverter;
//    }

    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {

        void sendToMqtt(String data);

    }

}
