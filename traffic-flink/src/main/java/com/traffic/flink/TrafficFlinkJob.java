package com.traffic.flink;

import com.traffic.flink.sink.MySQLSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TrafficFlinkJob {
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        MySQLSink dbSink = new MySQLSink();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("traffic-topic")
                .setGroupId("traffic-group")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        DataStream<String> alerts = stream.map(value -> {
            ObjectMapper mapper = new ObjectMapper();
            TrafficEvent event = mapper.readValue(value, TrafficEvent.class);
            
            double ratio = (double) event.speed / event.freeFlowSpeed;
            event.congestionLevel = ratio;

            if (ratio < 0.4) {
                // Save to MySQL only if congested
                dbSink.insertCongestion(event);
                return "🚨 SAVED TO DB: " + event.road + " (" + String.format("%.2f", ratio) + ")";
            }

            return "OK: " + event.road;
        });

        alerts.print();
        env.execute("Traffic Persistence Job");
    }
}