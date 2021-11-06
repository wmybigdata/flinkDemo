package com.wmy.flink.sql.flinksql.catalog.hive;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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
 * @Desription: flink 连接hive第一个案例
 * @Version: wmy-version-01
 */
public class Flink_Hive_Catalog_Demo01 {
    public static void main(String[] args) {
        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.创建HiveCatalog
        HiveCatalog hiveCatalog = new HiveCatalog("myHive", "wmy", "src/main/resources/");

        //3.注册HiveCatalog
        tableEnv.registerCatalog("myHive", hiveCatalog);

        //4.使用HiveCatalog
        tableEnv.useCatalog("myHive");

        //5.执行查询,查询Hive中已经存在的表数据
        tableEnv.executeSql("select * from t_access").print();
    }
}
