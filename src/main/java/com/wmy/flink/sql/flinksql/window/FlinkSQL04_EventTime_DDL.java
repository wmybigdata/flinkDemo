package com.wmy.flink.sql.flinksql.window;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.window
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 使用DDL的方式来指定事件时间字段
 * @Version: wmy-version-01
 */
public class FlinkSQL04_EventTime_DDL {
    public static void main(String[] args) {

        System.out.println("FlinkSQL04_EventTime_DDL");

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 使用DDL的方式来指定事件时间字段
        String ddlSQL = "create table source_sensor (" +
                "`id` string," +
                "`ts` bigint," +
                "`vc` int," +
                "rt as to_timestamp(from_unixtime(ts,'yyyy-MM-dd HH:mm:ss'))," +
                "watermark for rt as rt - interval '5' second" +
                ")" +
                "with (" +
                "'connector' = 'kafka'," +
                "'topic' = 'test'," +
                "'properties.bootstrap.servers' = 'flink01:9092'," +
                "'properties.group.id' = 'Flink_SQL_Kafka_Kafka'," +
                "'scan.startup.mode' = 'latest-offset'," +
                "'format' = 'csv'" +
                ")";
        tableEnv.executeSql(ddlSQL);
        Table source_sensor = tableEnv.from("source_sensor");

        /**
         * (
         *   `id` STRING,
         *   `ts` BIGINT,
         *   `vc` INT,
         *   `rt` TIMESTAMP(3) *ROWTIME* AS TO_TIMESTAMP(FROM_UNIXTIME(`ts`, 'yyyy-MM-dd HH:mm:ss')),
         *   WATERMARK FOR `rt`: TIMESTAMP(3) AS `rt` - INTERVAL '5' SECOND
         * )
         */
        source_sensor.printSchema();
        source_sensor.execute().print();
    }
}
