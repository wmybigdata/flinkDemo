package com.wmy.flink.sql.flinksql.fun.udf;

import lombok.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.functions.ScalarFunction;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;


/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.fun.udf
 * @Author: wmy
 * @Date: 2021/10/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: flink UDF 用户案例
 * @Version: wmy-version-01
 */
public class Flink_SQL_Fun_UDF_01 {
    public static void main(String[] args) throws Exception {

        System.out.println("Flink_SQL_Fun_UDF_01 >>> 2021-10-07");

        // 创建流式环境和表环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings environmentSettings = EnvironmentSettings
                .newInstance()
                .inStreamingMode()
                .useBlinkPlanner()
                .build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, environmentSettings);

        // 读取数据
        DataStreamSource<String> socketTextStream = env.socketTextStream("flink01", 8888);

        // 对数据进行转换
        SingleOutputStreamOperator<User> waterSensorDS = socketTextStream
                .map(new MapFunction<String, User>() {
                    @Override
                    public User map(String value) throws Exception {
                        String[] fields = value.split(",");
                        return User.builder()
                                        .id(fields[0])
                                        .name(fields[1])
                                        .age(Integer.parseInt(fields[2]))
                                        .build();
                    }
                });

        //3.将流转换为动态表
        Table table = tableEnv.fromDataStream(waterSensorDS);

        // 不注册就进行使用
        //table.select($("id"), $("name"), call(UserNameLength.class, $("name")).as("nameLength"), $("age")).execute().print();

        // 注册在进行使用
        tableEnv.createTemporarySystemFunction("nameLength", UserNameLength.class);
        tableEnv.sqlQuery("select id,name,age,nameLength(name) as nameLength from " + table).execute().print();

        // 执行流式环境
        env.execute("Flink_SQL_Fun_UDF_01 >>> 2021-10-07");
    }

    // define function logic
    public static class UserNameLength extends ScalarFunction {
        public int eval(String value) {
            return value.length();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class User {
        private String id;
        private String name;
        private int age;
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
