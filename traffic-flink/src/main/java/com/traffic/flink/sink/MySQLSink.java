package com.traffic.flink.sink;

import com.traffic.flink.TrafficEvent; // Updated to match your model package
import com.traffic.flink.model.RoadAnalytics;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MySQLSink implements Serializable {

    private static final String URL = "jdbc:mysql://localhost:3306/traffic_db";
    private static final String USER = "traffic_user";
    private static final String PASS = "Zain123!";

    // Shared method to get a connection
    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void insertCongestion(TrafficEvent e) {
        String sql = "INSERT INTO congestion_events " +
                     "(road, city, speed, free_flow_speed, congestion_level, timestamp) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            stmt.setString(1, e.road);
            stmt.setString(2, e.city);
            stmt.setInt(3, e.speed);
            stmt.setInt(4, e.freeFlowSpeed);
            stmt.setDouble(5, e.congestionLevel);
            stmt.setLong(6, e.timestamp);

            stmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Congestion DB Error: " + ex.getMessage());
        }
    }

    public void insertAnalytics(RoadAnalytics a) {
        String sql = "INSERT INTO road_analytics " +
                     "(road, avg_speed, avg_congestion, window_start, window_end) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, a.road);
            stmt.setDouble(2, a.avgSpeed);
            stmt.setDouble(3, a.avgCongestion);
            stmt.setLong(4, a.windowStart);
            stmt.setLong(5, a.windowEnd);

            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Analytics DB Error: " + e.getMessage());
        }
    }
}