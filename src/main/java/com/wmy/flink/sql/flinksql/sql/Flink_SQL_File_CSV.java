package com.wmy.flink.sql.flinksql.sql;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;

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
public class Flink_SQL_File_CSV {
    public static void main(String[] args) {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        tableEnv.executeSql( "create table student ("
                + "id VARCHAR,"
                + "name VARCHAR,"
                + "age VARCHAR"
                + ")"
                + "with ("
                + "'connector' = 'filesystem',"
                + "'path' = 'src/main/resources/02-User.csv',"
                + "'format' = 'csv'"
                + ")");
        Table student = tableEnv.from("student");
        student.execute().print();
    }
}
