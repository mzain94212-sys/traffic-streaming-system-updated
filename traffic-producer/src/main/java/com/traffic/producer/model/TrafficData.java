package com.traffic.producer.model;

public class TrafficData {
    public String road;
    public String city;
    public int speed;
    public int freeFlowSpeed;
    public double congestionLevel;
    public long timestamp;

    public TrafficData(String road, String city, int speed, int freeFlowSpeed) {
        this.road = road;
        this.city = city;
        this.speed = speed;
        this.freeFlowSpeed = freeFlowSpeed;
        this.congestionLevel = (double) speed / freeFlowSpeed;
        this.timestamp = System.currentTimeMillis();
    }
}
