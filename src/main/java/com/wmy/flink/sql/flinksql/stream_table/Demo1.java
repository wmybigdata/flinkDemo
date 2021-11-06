package com.wmy.flink.sql.flinksql.stream_table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.chapter01.stream_table
 * @Author: wmy
 * @Date: 2021/9/22
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink SQL 案例演示
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {
        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 设置检查点等等 ……

        // 读取数据源
        DataStreamSource<String> socketTextStream = env.socketTextStream("flink01", 8888);

        // 数据转换
        SingleOutputStreamOperator<Person> mapDS = socketTextStream.map(new MapFunction<String, Person>() {
            @Override
            public Person map(String value) throws Exception {
                String[] fields = value.split(",");
                return Person
                        .builder()
                        .id(fields[0])
                        .name(fields[1])
                        .age(Integer.parseInt(fields[2]))
                        .build();
            }
        });

        Table table = tableEnv.fromDataStream(mapDS);

        System.out.println("打印流表的schema信息。。。");
        table.printSchema();


        /*Table result = table
                .where($("id").isEqual("1001"))
                .select($("id"), $("name"), $("age"));
        //result.execute().print();
        DataStream<Person> personDataStream = tableEnv.toAppendStream(result, Person.class);
        personDataStream.print();*/

        Table result1 = table
                .where($("age").isGreaterOrEqual(21))
                .groupBy($("id"))
                .aggregate($("age").avg().as("avg_age"))
                .select($("id"), $("avg_age"));

        DataStream<Tuple2<Boolean, Row>> tuple2DataStream = tableEnv.toRetractStream(result1, Row.class);
        tuple2DataStream.print();

        env.execute();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Person {
        private String id;
        private String name;
        private int age;
    }
}
