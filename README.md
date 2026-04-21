# Real-Time Traffic Streaming System

## Overview
This project is a simple but powerful real-time data pipeline built using Kafka, Flink, Spring Boot, and MySQL. The idea is to simulate random fake traffic data genrated through code, process it in real time, and store useful insight in database.

## What it does

Generates traffic data every 3 seconds.
Sends it to Kafka topic.
Flink processes the data and detects congestion.
Stores results in MySQL.

## Architecture
Spring Boot → Kafka → Flink → MySQL

## Tech Used
Java (Spring Boot)
Apache Kafka
Apache Flink
MySQL
WSL Ubuntu

## How to Run
1. Start Kafka
bin/zookeeper-server-start.sh config/zookeeper.properties bin/kafka-server-start.sh config/server.properties

2. Run Producer
cd traffic-producer mvn spring-boot:run

3. Start Flink
./bin/start-cluster.sh

4. Run Flink Job
cd traffic-flink mvn clean package

../flink/bin/flink run
-c com.traffic.flink.TrafficFlinkJob
target/traffic-flink-1.0.jar

Example Output
Console: CONGESTION on Main Blvd OK Mall Road

## MySQL:
Stores congestion events for analysis

## Updation: Added the store-analytics feature which compute results in 1 minute window cycle.
