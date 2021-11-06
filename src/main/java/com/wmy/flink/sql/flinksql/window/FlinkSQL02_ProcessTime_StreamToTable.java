package com.wmy.flink.sql.flinksql.window;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.window
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 通过DDL来进行处理数据
 * @Version: wmy-version-01
 */
public class FlinkSQL02_ProcessTime_StreamToTable {
    public static void main(String[] args) {
        System.out.println("FlinkSQL02_ProcessTime_StreamToTable");
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 使用DDL的方式来处理时间字段
        String ddlSQL = "create table source_sensor (" +
                "`id` string," +
                "`ts` bigint," +
                "`vc` int," +
                "pt AS PROCTIME()" + // 主要是这个函数PROCTIME()，处理时间是不需要和数据产生什么关联
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

        // 打印shema信息
        /**
         * (
         *   `id` STRING,
         *   `ts` BIGINT,
         *   `vc` INT,
         *   `pt` TIMESTAMP_LTZ(3) NOT NULL *PROCTIME* AS PROCTIME()
         * )
         */
        source_sensor.printSchema();

    }
}
