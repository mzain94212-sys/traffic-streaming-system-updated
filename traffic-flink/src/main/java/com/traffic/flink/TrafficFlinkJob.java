package com.traffic.flink;

import com.traffic.flink.model.RoadAnalytics;
import com.traffic.flink.sink.MySQLSink;
// Make sure this import matches your file location
import com.traffic.flink.TrafficEvent; 

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class TrafficFlinkJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("traffic-topic")
                .setGroupId("flink-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

        // 1. Parse JSON to Object (Fixed: Local Gson initialization)
        DataStream<TrafficEvent> parsed = stream.map(json -> {
            com.google.gson.Gson localGson = new com.google.gson.Gson();
            return localGson.fromJson(json, TrafficEvent.class);
        });

        // 2. IMMEDIATE LAYER: Check for congestion and log to console/DB
        parsed.map(event -> {
            double ratio = (double) event.speed / event.freeFlowSpeed;
            event.congestionLevel = ratio;
            if (ratio < 0.4) {
                // Initializing sink locally to ensure serializability
                MySQLSink sink = new MySQLSink();
                sink.insertCongestion(event);
                return "🚨 CONGESTION: " + event.road + " (" + String.format("%.2f", ratio) + ")";
            }
            return "NORMAL: " + event.road;
        }).print();

        // 3. ANALYTICS LAYER: 1-Minute Windows per Road
        parsed.keyBy(event -> event.road)
                .window(TumblingProcessingTimeWindows.of(Time.minutes(1)))
                .apply(new WindowFunction<TrafficEvent, RoadAnalytics, String, TimeWindow>() {
                    @Override
                    public void apply(String road, TimeWindow window, Iterable<TrafficEvent> input, Collector<RoadAnalytics> out) {
                        int count = 0;
                        double sumSpeed = 0;
                        double sumCongestion = 0;

                        for (TrafficEvent e : input) {
                            sumSpeed += e.speed;
                            sumCongestion += (double) e.speed / e.freeFlowSpeed;
                            count++;
                        }

                        if (count > 0) {
                            out.collect(new RoadAnalytics(
                                    road, 
                                    sumSpeed / count, 
                                    sumCongestion / count, 
                                    window.getStart(), 
                                    window.getEnd()
                            ));
                        }
                    }
                })
                .map(analytics -> {
                    MySQLSink sink = new MySQLSink();
                    sink.insertAnalytics(analytics);
                    return "📊 Analytics Saved for: " + analytics.road;
                }).print();

        env.execute("Traffic Intelligence System");
    }
}