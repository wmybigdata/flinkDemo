package com.wmy.flink.sql.flinksql.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.sql
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink SQL读取kafka然后写入kafka
 * @Version: wmy-version-01
 */
public class Flink_SQL_Kafka_Kafka {
    public static void main(String[] args) throws Exception {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 注册sourceTable
        // 1001,14,14
        String sql1 = "create table source_sensor (\n" +
                "`id` string,\n" +
                "`ts` bigint,\n" +
                "`vc` int\n" +
                ")\n" +
                "with (\n" +
                "'connector' = 'kafka',\n" +
                "'topic' = 'test',\n" +
                "'properties.bootstrap.servers' = 'flink01:9092',\n" +
                "'properties.group.id' = 'Flink_SQL_Kafka_Kafka',\n" +
                "'scan.startup.mode' = 'latest-offset',\n" +
                "'format' = 'csv'\n" +
                ")";
        tableEnv.executeSql(sql1);

        //{"id":"1001","ts":14,"vc":14}
        String sql2 = "create table sink_sensor(\n" +
                "`id` string,\n" +
                "`ts` bigint,\n" +
                "`vc` int\n" +
                ") with (\n" +
                "'connector' = 'kafka',\n" +
                "'topic' = 'test1',\n" +
                "'properties.bootstrap.servers' = 'flink01:9092',\n" +
                "'format' = 'json'\n" +
                ")";

        tableEnv.executeSql(sql2);

        // 插入语句
        tableEnv.executeSql("insert into sink_sensor select * from source_sensor where id = '1001'");

//        env.execute("Flink_SQL_Kafka_Kafka");

    }
}
