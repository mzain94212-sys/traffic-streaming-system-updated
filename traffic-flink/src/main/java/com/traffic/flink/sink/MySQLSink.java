package com.traffic.flink.sink;

import com.traffic.flink.TrafficEvent;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class MySQLSink implements Serializable {

    private static final String URL = "jdbc:mysql://localhost:3306/traffic_db";
    private static final String USER = "root";
    private static final String PASS = "root123"; // Usually empty for root on local WSL

    public void insertCongestion(TrafficEvent e) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "INSERT INTO congestion_events " +
                         "(road, city, speed, free_flow_speed, congestion_level, timestamp) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, e.road);
            stmt.setString(2, e.city);
            stmt.setInt(3, e.speed);
            stmt.setInt(4, e.freeFlowSpeed);
            stmt.setDouble(5, e.congestionLevel);
            stmt.setLong(6, e.timestamp);

            stmt.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Database Error: " + ex.getMessage());
        }
    }
}
