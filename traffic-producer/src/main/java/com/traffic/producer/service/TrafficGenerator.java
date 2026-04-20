package com.traffic.producer.service;
import com.traffic.producer.model.TrafficData;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class TrafficGenerator {
    private final Random random = new Random();
    private final String[] roads = {"Main Blvd", "Mall Road", "Jinnah Avenue", "F-10 Road"};

    public TrafficData generate() {
        String road = roads[random.nextInt(roads.length)];
        int freeFlowSpeed = 60;
        int speed = 10 + random.nextInt(50);
        return new TrafficData(road, "Islamabad", speed, freeFlowSpeed);
    }
}
