package com.traffic.producer.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traffic.producer.model.TrafficData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    private static final String TOPIC = "traffic-topic";
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendTrafficData(TrafficData data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            kafkaTemplate.send(TOPIC, json);
            System.out.println("Sent → " + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
