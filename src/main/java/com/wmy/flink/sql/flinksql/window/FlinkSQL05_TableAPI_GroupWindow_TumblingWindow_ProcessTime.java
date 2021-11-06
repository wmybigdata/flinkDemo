package com.wmy.flink.sql.flinksql.window;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Tumble;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

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
public class FlinkSQL05_TableAPI_GroupWindow_TumblingWindow_ProcessTime {
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
                "pt AS PROCTIME()" +
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

        Table result = source_sensor
                .window(Tumble.over(lit(5).seconds()).on($("pt")).as("tw"))
                .groupBy($("id"), $("tw"))
                .select($("id"), $("id").count());

        result.execute().print();
    }
}
