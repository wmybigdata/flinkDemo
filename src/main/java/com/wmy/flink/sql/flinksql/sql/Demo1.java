package com.wmy.flink.sql.flinksql.sql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.sql
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 使用SQL的方式
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {
        System.out.println("Demo1");

        //1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //2.读取端口数据转换为JavaBean
        SingleOutputStreamOperator<WaterSensor> waterSensorDS = env.socketTextStream("flink01", 8888)
                .map(data -> {
                    String[] split = data.split(",");
                    return new WaterSensor(split[0], Long.parseLong(split[1]), Integer.parseInt(split[2]));
                });

        //3.将流转换为动态表
//        Table table = tableEnv.fromDataStream(waterSensorDS);

        //4.使用SQL查询未注册的表
//        Table result = tableEnv.sqlQuery("select id,sum(vc) from " + table + " group by id");
//        result.execute().print();
//
//        Table table1 = tableEnv.sqlQuery("select id,ts,vc from " + table + " where id ='1001'");
//        table1.execute().print();

        tableEnv.createTemporaryView("sensor",waterSensorDS);

        Table table = tableEnv.sqlQuery("select id,count(ts) ct,sum(vc) vc from sensor group by id");
//        table.execute().print();

        tableEnv.toRetractStream(table, Row.class).print();

        env.execute("Demo1");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WaterSensor {
        private String id;
        private Long ts;
        private Integer vc;
    }
}
