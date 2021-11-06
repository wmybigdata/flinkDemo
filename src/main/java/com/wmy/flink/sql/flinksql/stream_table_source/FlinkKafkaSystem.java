package com.wmy.flink.sql.flinksql.stream_table_source;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Csv;
import org.apache.flink.table.descriptors.Kafka;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.chapter01.stream_table_source
 * @Author: wmy
 * @Date: 2021/9/23
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink读取kafka文件系统
 * @Version: wmy-version-01
 */
public class FlinkKafkaSystem {
    public static void main(String[] args) throws Exception {

        // flink创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // Flink读取kafka外部数据源
        tableEnv
                .connect(
                        new Kafka()
                                .version("universal")
                                .topic("test")
                                .startFromLatest()
                                .property(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "flink01:9092")
                                .property(ConsumerConfig.GROUP_ID_CONFIG, "FlinkKafkaSystem")
                )
                .withSchema(
                        new Schema()
                                .field("id", DataTypes.STRING())
                                .field("name", DataTypes.STRING())
                                .field("age", DataTypes.INT())
                )
                .withFormat(new Csv())
                .createTemporaryTable("user");

        // 转换为表
        Table user = tableEnv.from("user");

        // 查询数据
        Table resultTable = user
                .groupBy($("id"))
                .select($("id"), $("id").count().as("id_count"));

        // 只能使用toRetractStream（insert delete）
        // toAppendStream: insert
        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEnv.toRetractStream(resultTable, Row.class);
        tuple2DataStream.print();

        env.execute();
    }
}
