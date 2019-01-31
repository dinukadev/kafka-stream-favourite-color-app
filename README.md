# Introduction

This is a sample application to calcualte the favourite color which is input on a Kafka topic. The application is developed with Kafka Streams.

## Pre-requisites

Make sure to create the topics needed to run this application with the following commands

```
./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic favourite-color-input

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --config cleanup.policy=compact --topic favourite-color-with-key-value 

./kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --config cleanup.policy=compact --topic favourite-color-output

```

## Running the application

- Start Zookeeper
- Start Kafka
- Bring up a consumer with the following command;
```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic favourite-color-output --from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```
- Run the main Java class found withing this project
- Bring up a producer with the following command;
```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic favourite-color-input --from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
```
- On the same producer terminal, type in the following;

```
jon,blue
reece,green
ray,red
drew,blue
jon,red
```
- On the consumer, you can see the counts changing with your input



