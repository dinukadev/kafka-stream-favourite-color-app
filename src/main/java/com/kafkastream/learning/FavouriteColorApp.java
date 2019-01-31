package com.kafkastream.learning;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Properties;

/**
 * This is the main class for counting the favourite colors.
 * <p>
 * The colors are filtered for either red,green or blue
 */
public class FavouriteColorApp {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String> lineStream = builder.stream("favourite-color-input");
        KStream<String, String> filteredStream = lineStream.filter((key, val) -> val.contains(","))
                .selectKey((key, val) -> val.split(",")[0])
                .mapValues((val) -> val.split(",")[1].toLowerCase())
                .filter((key, val) -> val.matches("green|blue|red"));

        filteredStream.to("favourite-color-with-key-value");

        KTable<String, String> favColorTable = builder.table("favourite-color-with-key-value");

        /**
         * With KTables the last change is maintained so that we can retrieve the last value and look up the count for the last value
         */
        KTable<String, Long> colorCountedTable = favColorTable
                .groupBy((key, val) -> new KeyValue<>(val, val))
                .count("ColorCount");
        colorCountedTable.to(Serdes.String(), Serdes.Long(), "favourite-color-output");

        KafkaStreams kafkaStreams = new KafkaStreams(builder, config);
        kafkaStreams.cleanUp();
        kafkaStreams.start();


        System.out.println("Topology: " + kafkaStreams.toString());

        /**
         * Finally clean up the stream on shutdown graceully with a shutdownhook
         */
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

    }
}
