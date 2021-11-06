package com.wmy.flink.dataStream.join;

import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.join
 * @Author: wmy
 * @Date: 2021/9/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 演示了试用异步IO来访问维表：
 * @Version: wmy-version-01
 */
public class JoinDemo3 {
    public static void main(String[] args) throws Exception {
        System.out.println("JoinDemo3 >>> 2021-09-07");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(10);

        // 获取主流中的数据
        SingleOutputStreamOperator<Tuple2<String, Integer>> mainStream = env
                .socketTextStream("flink04", 8888)
                .map(line -> {
                    String[] fields = line.split(",");
                    return new Tuple2<String, Integer>(fields[0], Integer.valueOf(fields[1]));
                })
                .returns(new TypeHint<Tuple2<String, Integer>>() {
                });

        SingleOutputStreamOperator<Tuple3<String, Integer, String>> orderResult = AsyncDataStream
                // 保证顺序，异步返回的结果保证顺序，超时时间1秒，最大的容量2，超出容量触发反压
                .orderedWait(
                        // 允许乱序，异步返回的结果允许乱序，超时时间1秒，最大容量2，超出容量触发反压
                        mainStream,
                        new JoinDemo3AyncFunction(),
                        1000L,
                        TimeUnit.SECONDS,
                        5
                );

        DataStream<Tuple3<String,Integer, String>> unorderedResult = AsyncDataStream
                //允许乱序：异步返回的结果允许乱序，超时时间1秒，最大容量2，超出容量触发反压
                .unorderedWait(mainStream, new JoinDemo3AyncFunction(), 1000L, TimeUnit.MILLISECONDS, 2)
                .setParallelism(1);

        orderResult.print("orderResult >>> ");
        unorderedResult.print("unorderedResult >>> ");
        env.execute("JoinDemo3 >>> 2021-09-07");
    }

    public static class JoinDemo3AyncFunction extends RichAsyncFunction<Tuple2<String, Integer>, Tuple3<String, Integer, String>> {

        // 链接
        private static String jdbcUrl = "jdbc:mysql://flink04:3306?useSSL=false";
        private static String username = "root";
        private static String password = "000000";
        private static String driverName = "com.mysql.jdbc.Driver";
        java.sql.Connection conn;
        PreparedStatement ps;

        @Override
        public void open(Configuration parameters) throws Exception {
            Class.forName(driverName);
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            ps = conn.prepareStatement("select city_name from bigdata.city_info where id = ?");
        }

        @Override
        public void close() throws Exception {
            ps.close();
            conn.close();
        }

        @Override
        public void asyncInvoke(Tuple2<String, Integer> input, ResultFuture<Tuple3<String, Integer, String>> resultFuture) throws Exception {
            // 使用city id 查询
            ps.setInt(1, input.f1);
            ResultSet resultSet = ps.executeQuery();
            String cityName = "";
            if (resultSet.next()) {
                cityName = resultSet.getString(1);
            }

            List list = new ArrayList<Tuple3<String, Integer, String>>();
            list.add(new Tuple3<>(input.f0, input.f1, cityName));
            resultFuture.complete(list);
        }

        //超时处理
        @Override
        public void timeout(Tuple2<String, Integer> input, ResultFuture<Tuple3<String,Integer, String>> resultFuture) throws Exception {
            List list = new ArrayList<Tuple3<String, Integer, String>>();
            list.add(new Tuple3<>(input.f0,input.f1, "")); // 超时的就输出null值就好了
            resultFuture.complete(list);
        }
    }
}
