package com.traffic.flink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrafficEvent {
    public String road;
    public String city;
    public int speed;
    public int freeFlowSpeed;
    public double congestionLevel;
    public long timestamp;

    public TrafficEvent() {} // Necessary for Jackson
}
