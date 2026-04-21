package com.traffic.flink.model;

import java.io.Serializable;

public class RoadAnalytics implements Serializable {
    public String road;
    public double avgSpeed;
    public double avgCongestion;
    public long windowStart;
    public long windowEnd;

    public RoadAnalytics() {}

    public RoadAnalytics(String road, double avgSpeed, double avgCongestion,
                         long windowStart, long windowEnd) {
        this.road = road;
        this.avgSpeed = avgSpeed;
        this.avgCongestion = avgCongestion;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
    }
}
