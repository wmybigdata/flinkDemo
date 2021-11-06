package com.wmy.flink.sql.flinksql.window;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.window
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 事件时间
 * @Version: wmy-version-01
 */
public class FlinkSQL03_EventTime_StreamToTable {
    public static void main(String[] args) {
        System.out.println("FlinkSQL01_ProcessTime_StreamToTable");

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 提前时间戳字段
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
                .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    @Override
                    public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                        return element.getTs() * 1000L;
                    }
                });

        // 读取文本数据并转换为JavaBean对象,这个是携带了watermark
        SingleOutputStreamOperator<WaterSensor> waterSensorMarkDS = env
                .readTextFile("src/main/resources/03-sensor.txt")
                .map(new MapFunction<String, WaterSensor>() {
                    @Override
                    public WaterSensor map(String value) throws Exception {
                        String[] fileds = value.split(",");
                        return WaterSensor.builder()
                                .id(fileds[0])
                                .ts(Long.parseLong(fileds[1]))
                                .vc(Integer.parseInt(fileds[2]))
                                .build();
                    }
                }).assignTimestampsAndWatermarks(watermarkStrategy);

        // 将流转换为表指定事件时间字段
        Table table = tableEnv
                .fromDataStream(
                        waterSensorMarkDS,
                        $("id"),
                        $("ts"),
                        $("vc"),
                        $("rt").rowtime()
                );

        /**
         * (
         *   `id` STRING,
         *   `ts` BIGINT,
         *   `vc` INT,
         *   `rt` TIMESTAMP(3) *ROWTIME*
         * )
         */
        table.printSchema();


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WaterSensor {
        private String id;
        private Long ts;
        private Integer vc;
    }
}
