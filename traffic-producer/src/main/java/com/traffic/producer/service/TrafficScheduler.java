package com.traffic.producer.service;
import com.traffic.producer.model.TrafficData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrafficScheduler {
    @Autowired
    private TrafficGenerator generator;
    @Autowired
    private KafkaProducerService producerService;

    @Scheduled(fixedRate = 3000)
    public void sendData() {
        TrafficData data = generator.generate();
        producerService.sendTrafficData(data);
    }
}
