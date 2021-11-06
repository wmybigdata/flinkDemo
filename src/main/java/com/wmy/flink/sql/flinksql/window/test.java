package com.wmy.flink.sql.flinksql.window;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.text.DecimalFormat;
import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.lit;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.window
 * @Author: wmy
 * @Date: 2021/9/28
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class test {
    public static void main(String[] args) throws Exception {
        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        WatermarkStrategy<Bid> waterSensorWatermarkStrategy = WatermarkStrategy.<Bid>forBoundedOutOfOrderness(Duration.ofMillis(10))
                .withTimestampAssigner(new SerializableTimestampAssigner<Bid>() {
                    @Override
                    public long extractTimestamp(Bid element, long recordTimestamp) {
                        return element.getBidtime() * 1000L;
                    }
                });

        /**
         * 1586909100,4.00,C,supplier1
         * 1586909220,2.00,A,supplier1
         * 1586909340,5.00,D,supplier2
         * 1586909460,3.00,B,supplier2
         * 1586909580,1.00,E,supplier1
         * 1586909820,6.00,F,supplier2
         */

        // 使用DDL的方式来指定事件时间字段
        // Flink读取kafka外部数据源
        SingleOutputStreamOperator<Bid> socketDS = env.socketTextStream("flink01", 8888)
                .map(new MapFunction<String, Bid>() {
                    @Override
                    public Bid map(String value) throws Exception {
                        String[] fields = value.split(",");
                        return Bid.builder()
                                .bidtime(Long.parseLong(fields[0]))
                                .price(Float.parseFloat(fields[1]))
                                .item(fields[2])
                                .build();
                    }
                }).assignTimestampsAndWatermarks(waterSensorWatermarkStrategy);

        Table table = tableEnv.fromDataStream(
                socketDS
        );

        table.execute().print();

        env.execute();
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Bid {
        private long bidtime;
        private float price;
        private String item;
    }
}
