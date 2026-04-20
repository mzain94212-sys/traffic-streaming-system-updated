package com.traffic.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // IMPORTANT

@SpringBootApplication
@EnableScheduling // IMPORTANT
public class TrafficProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrafficProducerApplication.class, args);
    }
}