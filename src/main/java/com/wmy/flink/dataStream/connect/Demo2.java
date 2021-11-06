package com.wmy.flink.dataStream.connect;

import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;

import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.formats.json.JsonRowDataDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.twill.kafka.client.KafkaConsumer;


import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.connect
 * @Author: wmy
 * @Date: 2021/10/18
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Apache kafka connector 连接器
 * @Version: wmy-version-01
 */
public class Demo2 {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        WatermarkStrategy<String> objectWatermarkStrategy = WatermarkStrategy.<String>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                .withTimestampAssigner(new SerializableTimestampAssigner<String>() {
                    @Override
                    public long extractTimestamp(String element, long recordTimestamp) {
                        return 0;
                    }
                });

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers("flink01:9092")
                .setTopics("test")
                .setGroupId("my-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();




        DataStreamSource<String> kafka_source = env.fromSource(source, objectWatermarkStrategy, "Kafka Source");

        env.execute();
    }
}
