package com.wmy.flink.dataStream.join;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.join
 * @Author: wmy
 * @Date: 2021/9/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * 这个例子是从socket中读取的流，数据为用户名称和城市id，维表是城市id、城市名称，
 * 主流和维表关联，得到用户名称、城市id、城市名称
 * 这个例子采用在RichMapfunction类的open方法中将维表数据加载到内存
 * @Version: wmy-version-01
 */
public class JoinDemo1 {
    public static void main(String[] args) throws Exception {
        System.out.println("JoinDemo1 ---> 20210907");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<Tuple2<String, Integer>> textStream = env.socketTextStream("flink04", 8888, "\n")
                .map(line -> {
                    String[] fields = line.split(",");
                    return new Tuple2<String, Integer>(fields[0], Integer.valueOf(fields[1]));
                }).returns(new TypeHint<Tuple2<String, Integer>>() {
                });
        SingleOutputStreamOperator<Tuple3<String, Integer, String>> streamOperator = textStream.map(new MapJoinDemo());
        streamOperator.print();
        env.execute("JoinDemo1");
    }

    public static class MapJoinDemo extends RichMapFunction<Tuple2<String, Integer>, Tuple3<String, Integer, String>> {

        private Map<Integer,String> hashMap;


        @Override

        public void open(Configuration parameters) throws Exception {
            hashMap = new HashMap<Integer, String>();
            hashMap.put(1001, "beijing");
            hashMap.put(1002, "shanghai");
            hashMap.put(1003, "wuhan");
            hashMap.put(1004, "changsha");
        }

        @Override
        public Tuple3<String, Integer, String> map(Tuple2<String, Integer> value) throws Exception {
            String cityName = "";
            if (hashMap.containsKey(value.f1)) {
                cityName = hashMap.get(value.f1);
            }
            return new Tuple3<>(value.f0, value.f1, cityName);// 用户ID，城市ID，城市名称
        }
    }
}
