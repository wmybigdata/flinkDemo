package com.wmy.flink.sql.flinksql.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.sql
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink读取kafka数据写入到MySQL中
 * @Version: wmy-version-01
 */
public class Flink_SQL_Kafka_MySQL {
    public static void main(String[] args) throws Exception {
        System.out.println("Flink_SQL_Kafka_MySQL");
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 注册sourceTable
        // 1001,14,14
        String sql1 = "create table source_sensor ("
                + "`id` string,"
                + "`ts` bigint,"
                + "`vc` int"
                + ")"
                + " with ("
                + "'connector' = 'kafka',"
                + "'topic' = 'test',"
                + "'properties.bootstrap.servers' = 'flink01:9092',"
                + "'properties.group.id' = 'Flink_SQL_Kafka_Kafka',"
                + "'scan.startup.mode' = 'latest-offset',"
                + "'format' = 'csv'"
                + ")";
        tableEnv.executeSql(sql1);
        Table source_sensor = tableEnv.from("source_sensor");

        String sql2 = "create table sink_sensor ("
                + "`id` string,"
                + "`ts` bigint,"
                + "`vc` int"
                + ")"
                + " with ("
                + "'connector' = 'jdbc',"
                + "'url' = 'jdbc:mysql://dm:3306/wmy',"
                + "'table-name' = 'sink_sensor',"
                + "'username' = 'root',"
                + "'password' = '000000'"
                + ")";

        tableEnv.executeSql(sql2);

        // 插入语句
//        tableEnv.executeSql("insert into sink_sensor select * from source_sensor");
        source_sensor.executeInsert("sink_sensor");

    }
}
