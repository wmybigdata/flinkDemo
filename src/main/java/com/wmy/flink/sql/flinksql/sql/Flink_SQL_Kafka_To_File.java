package com.wmy.flink.sql.flinksql.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.sql
 * @Author: wmy
 * @Date: 2021/9/28
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Flink_SQL_Kafka_To_File {
    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 注册sourceTable
        // 1001,14,14
        String sql = "create table student (" +
                "`id` string," +
                "`name` string," +
                "`age` int" +
                ")" +
                "with (" +
                "'connector' = 'kafka'," +
                "'topic' = 'test'," +
                "'properties.bootstrap.servers' = 'flink01:9092'," +
                "'properties.group.id' = 'Flink_SQL_Kafka_To_File'," +
                "'scan.startup.mode' = 'latest-offset'," +
                "'format' = 'csv'" +
                ")";
        tableEnv.executeSql(sql);

        // 转换成表,查询结果，处理
        Table student = tableEnv.from("student").as($("id"), $("name"), $("age"));
        Table select = student.where($("id").isEqual("1001"))
                .select($("id"), $("name"), $("age"));


        // 创建本地文件表
        tableEnv.executeSql("create table student_file (" +
                "`id` string," +
                "`name` string," +
                "`age` int" +
                ")" +
                "with (" +
                "'connector' = 'filesystem'," +
                "'path' = 'src/main/resources/student_file.csv'," +
                "'format' = 'csv'" +
                ")");

//        tableEnv.connect(
//                        new FileSystem()
//                                .path("src/main/resources/student_file.csv"))
//                .withSchema(new Schema()
//                        .field("id", DataTypes.STRING())
//                        .field("name", DataTypes.STRING())
//                        .field("age", DataTypes.INT())
//                )
//                .withFormat(new Csv())
//                .createTemporaryTable("student_file");

        // 插入语句
        select.executeInsert("student_file");
//        TableResult student_file = select.executeInsert("student_file");
//        student_file.print();
    }
}
