package com.wmy.flink.sql.flinksql.stream_table_source;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.FileSystem;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.table.types.DataType;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.chapter01.stream_table_source
 * @Author: wmy
 * @Date: 2021/9/23
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink SQL 读取filesystem文件的数据
 * @Version: wmy-version-01
 */
public class FlinkFileSystem {
    public static void main(String[] args) throws Exception {

        // 创建表
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 读取外部数据
        tableEnv
                .connect(new FileSystem().path("src/main/resources/02-User.csv"))
                .withSchema(
                        new Schema()
                                .field("id", DataTypes.STRING())
                                .field("name", DataTypes.STRING())
                                .field("age", DataTypes.INT())
                )
                .withFormat(new Csv().fieldDelimiter(',').lineDelimiter("\n"))
                .createTemporaryTable("user");

        // 将读取的外部系统文件，转换为表
        Table user = tableEnv.from("user");
        Table result = user
                .groupBy($("id"))
                .select($("id"), $("id").count().as("id_count"));
        result.execute().print();

    }
}
