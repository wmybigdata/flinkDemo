package com.wmy.flink.sql.flinksql.catalog.hive;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.catalog.hive
 * @Author: wmy
 * @Date: 2021/10/10
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: flink 使用 hive catalog在hive中创建表
 * @Version: wmy-version-01
 */
public class Flink_Hive_Catalog_Demo02 {
    public static void main(String[] args) {
        // 1、获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings environmentSettings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, environmentSettings);

        // 2、创建hiveCatalog
        HiveCatalog hiveCatalog = new HiveCatalog("myHive", "wmy", "src/main/resources");

        //3.注册HiveCatalog
        tableEnv.registerCatalog("myHive", hiveCatalog);

        //4.使用HiveCatalog
        tableEnv.useCatalog("myHive");

        // 3、创建catalog database
//        tableEnv.executeSql("CREATE TABLE mytable(name STRING, age INT) with (" +
//                "'connector.type' = 'kafka'," +
//                "'connector.version' = 'universal'," +
//                "'connector.topic' = 'test'," +
//                "'connector.properties.bootstrap.servers' = 'flink01:9092'," +
//                "'format.type' = 'csv'," +
//                "'update-mode' = 'append'" +
//                ")");
        tableEnv.listDatabases();


    }
}
