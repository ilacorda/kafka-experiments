package com.kafka.consumer;

import kafka.consumer.*; 
import kafka.consumer.KafkaStream;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import kafka.javaapi.consumer.ConsumerConnector;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("deprecation")
public class SampleConsumer {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put("zookeeper.connect", "localhost:2181");
        config.put("group.id", "default");
        config.put("partition.assignment.strategy", "roundrobin");
        config.put("bootstrap.servers", "localhost:9092");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        kafka.consumer.ConsumerConfig consumerConfig = new kafka.consumer.ConsumerConfig(config);


        ConsumerConnector consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(consumerConfig);

        Map<String, Integer> topicCountMap = new HashMap<>();
        topicCountMap.put("testTopic", 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);

        List<KafkaStream<byte[], byte[]>> streamList = consumerMap.get("testTopic");

        KafkaStream<byte[], byte[]> stream = streamList.get(0);

        @SuppressWarnings("deprecation")
		ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
        while (iterator.hasNext()) {
            System.out.println(new String(iterator.next().message()));
        }

    }

    public static void processRecords(Map<String, ConsumerRecords<String, String>> records) {
        List<ConsumerRecord<String, String>> messages = records.get("testTopic").records(new TopicPartition("testTopic", 1));
        if (messages != null) {
            for (ConsumerRecord<String, String> next : messages) {
                try {
                    System.out.println(next.value());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No messages");
        }
    }
}
