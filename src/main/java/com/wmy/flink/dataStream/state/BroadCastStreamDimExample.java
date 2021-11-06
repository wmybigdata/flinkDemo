package com.wmy.flink.dataStream.state;

import lombok.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.util.Collector;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.state
 * @Author: wmy
 * @Date: 2021/10/14
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 主流和维表进行关联
 * @Version: wmy-version-01
 */
public class BroadCastStreamDimExample {
    private static MapStateDescriptor<String, Dim> stateDescriptor = new MapStateDescriptor<>(
            "baseinfo-state",
            String.class,
            Dim.class
    );

    public static void main(String[] args) throws Exception {
        System.out.println("BroadCastStreamDimExample >>> 2021-10-14");

        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据
        DataStreamSource<String> socketTextStream = env.socketTextStream("flink01", 8888);

        // 对数据处理成javaBean对象
        SingleOutputStreamOperator<Fact> mapWithFactTableDS = socketTextStream.map(new MapFunction<String, Fact>() {
            @Override
            public Fact map(String value) throws Exception {
                String[] fields = value.split(",",-1);
                return Fact
                        .builder()
                        .curst_id(fields[0])
                        .cust_name(fields[1])
                        .cust_city(fields[2])
                        .cust_email(fields[3])
                        .build();
            }
        });

        // 读取维表的数据
        DataStreamSource<Map<String, Dim>> dimDS = env.addSource(new RichSourceFunction<Map<String, Dim>>() {
            private Connection connection;
            private PreparedStatement preparedStatement = null;
            private ResultSet resultSet;
            private String URL = "jdbc:mysql://dm:3306/wmy";
            private String DRIVER_CLASS = "com.mysql.jdbc.Driver";
            private String USERNAME = "root";
            private String PASSWORD = "000000";
            private Map<String, Dim> dimMap;

            @Override
            public void open(Configuration parameters) throws Exception {
                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                dimMap = new HashMap<>();
                System.out.println("创建MySQL的对象成功。。。" + URL);
            }

            @Override
            public void run(SourceContext<Map<String, Dim>> ctx) throws Exception {
                String sql = "SELECT `cust_id`,`cust_city`,`cust_email` FROM `customers`";
                preparedStatement = connection.prepareStatement(sql);
                resultSet = preparedStatement.executeQuery();
                while (this.resultSet.next()) {
                    String cust_id = this.resultSet.getString(1);
                    String cust_city = this.resultSet.getString(2);
                    String cust_email = this.resultSet.getString(3);

                    Dim dim = Dim.builder().curst_id(cust_id).cust_city(cust_city).cust_email(cust_email).build();
                    dimMap.put(cust_id, dim);

                    ctx.collect(dimMap);
                }
            }

            @Override
            public void cancel() {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (preparedStatement != null) {
                    try {
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        BroadcastStream<Map<String, Dim>> broadcastState = dimDS.broadcast(stateDescriptor);

        SingleOutputStreamOperator<Fact> processDS = mapWithFactTableDS.connect(broadcastState)
                .process(new BroadcastProcessFunction<Fact, Map<String, Dim>, Fact>() {
                    ReadOnlyBroadcastState<String, Dim> broadcastState;
                    Dim dim = null;

                    @Override
                    public void processElement(Fact value, BroadcastProcessFunction<Fact, Map<String, Dim>, Fact>.ReadOnlyContext ctx, Collector<Fact> out) throws Exception {
                        ReadOnlyBroadcastState<String, Dim> broadcastState = ctx.getBroadcastState(stateDescriptor);
                        dim = broadcastState.get(value.curst_id);
                        if (dim != null) {
                            value.cust_city = dim.cust_city;
                            value.cust_email = dim.cust_email;
                        }
                        out.collect(value);
                    }

                    @Override
                    public void processBroadcastElement(Map<String, Dim> value, BroadcastProcessFunction<Fact, Map<String, Dim>, Fact>.Context ctx, Collector<Fact> out) throws Exception {
                        BroadcastState<String, Dim> state = ctx.getBroadcastState(stateDescriptor);
                        state.clear();
                        state.putAll(value);
                    }
                });

        processDS.print();

        // 执行程序
        env.execute("BroadCastStreamDimExample >>> 2021-10-14");
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Fact {
        private String curst_id;
        private String cust_name;
        private String cust_city;
        private String cust_email;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    public static class Dim {
        private String curst_id;
        private String cust_city;
        private String cust_email;
    }
}
