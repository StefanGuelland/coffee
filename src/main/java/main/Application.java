package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@IntegrationComponentScan
@ComponentScan("coffee")
@ComponentScan("mqtt")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}