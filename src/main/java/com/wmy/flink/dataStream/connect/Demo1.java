package com.wmy.flink.dataStream.connect;

import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import javax.naming.Name;
import java.sql.*;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.connect
 * @Author: wmy
 * @Date: 2021/10/9
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 这个是测试把广播流变成connect流是否能够解决问题
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> socketTextStream = env.socketTextStream("flink01", 8888);
        SingleOutputStreamOperator<User> userDS = socketTextStream.map(new MapFunction<String, User>() {
            @Override
            public User map(String value) throws Exception {
                String[] fields = value.split(",");
                return User.builder()
                        .id(fields[0])
                        .name(fields[1])
                        .build();
            }
        });

        DataStreamSource<Tuple2<String, Integer>> userMysqlDS = env.addSource(new RichSourceFunction<Tuple2<String,Integer>>() {

            private transient Connection conn = null;
            private transient PreparedStatement preparedStatement = null;
            private boolean flag = true;
            private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
            private static final String URL = "jdbc:mysql://dm:3306/wmy";
            private static final String USERNAME = "root";
            private static final String PASSWORD = "000000";
            ResultSet resultSet = null;

            Tuple2<String,Integer> tuple2;

            @Override
            public void open(Configuration parameters) throws Exception {
                Class.forName(DRIVER_CLASS);
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }

            @Override
            public void run(SourceContext<Tuple2<String,Integer>> ctx) throws Exception {
                while (true) {
                    try {
                        String sql = "select id,salary from user";
                        preparedStatement = conn.prepareStatement(sql);
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            String id = resultSet.getString("id");
                            int salary = resultSet.getInt("salary");
                            tuple2 = new Tuple2<>(id,salary);
                            ctx.collect(tuple2);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }
                        Thread.sleep(1000L);
                    }
                }
            }

            @Override
            public void cancel() {

            }
        });

        SingleOutputStreamOperator<Person> tuple2ValueState = userDS.connect(userMysqlDS).keyBy(x -> x.id, y -> y.f0).process(new CoProcessFunction<User, Tuple2<String, Integer>, Person>() {

            // 定义保存MySQL里面的数据
            private ValueState<Tuple2<String, Integer>> tuple2ValueState;

            @Override
            public void open(Configuration parameters) throws Exception {
                tuple2ValueState = getRuntimeContext().getState(new ValueStateDescriptor<Tuple2<String, Integer>>("tuple2ValueState", TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {
                })));
            }

            @Override
            public void processElement1(User value, CoProcessFunction<User, Tuple2<String, Integer>, Person>.Context ctx, Collector<Person> out) throws Exception {
                Tuple2<String, Integer> value1 = tuple2ValueState.value();
                if (value1 != null) {
                    Person build = Person.builder().id(value.getId()).name(value.getName()).salary(value1.f1).build();
                    out.collect(build);
                }
            }

            @Override
            public void processElement2(Tuple2<String, Integer> value, CoProcessFunction<User, Tuple2<String, Integer>, Person>.Context ctx, Collector<Person> out) throws Exception {
                tuple2ValueState.update(value);
            }
        });

        tuple2ValueState.print();


        env.execute();
    }

    @Data
    @AllArgsConstructor
    @ToString
    @Builder
    public static class User {
        private String id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    @ToString
    @Builder
    private static class Person {
        private String id;
        private String name;
        private int salary;
    }
}
