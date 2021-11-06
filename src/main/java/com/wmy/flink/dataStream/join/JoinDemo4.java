package com.wmy.flink.dataStream.join;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.join
 * @Author: wmy
 * @Date: 2021/9/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 这个例子是从socket中读取的流，数据为用户名称和城市id，维表是城市id、城市名称，
 * 主流和维表关联，得到用户名称、城市id、城市名称
 * 这个例子采用 Flink 广播流的方式来做为维度
 * @Version: wmy-version-01
 */
public class JoinDemo4 {
    public static void main(String[] args) throws Exception {
        System.out.println("JoinDemo4 >>> 2021-09-07 ");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(10);

        //定义主流
        DataStream<Tuple2<String, Integer>> textStream = env.socketTextStream("flink04", 9999, "\n")
                .map(p -> {
                    //输入格式为：user,1000,分别是用户名称和城市编号
                    String[] list = p.split(",");
                    return new Tuple2<String, Integer>(list[0], Integer.valueOf(list[1]));
                })
                .returns(new TypeHint<Tuple2<String, Integer>>() {
                });

        //定义城市流
        DataStream<Tuple2<Integer, String>> cityStream = env.socketTextStream("flink04", 8888, "\n")
                .map(p -> {
                    //输入格式为：城市ID,城市名称
                    String[] list = p.split(",");
                    return new Tuple2<Integer, String>(Integer.valueOf(list[0]), list[1]);
                })
                .returns(new TypeHint<Tuple2<Integer, String>>() {
                });

        // 定义广播流
        MapStateDescriptor<Integer, String> dimMapStateDescriptor = new MapStateDescriptor<>("dim", Integer.class, String.class);
        BroadcastStream<Tuple2<Integer, String>> broadcastStream = cityStream.broadcast(dimMapStateDescriptor);

        SingleOutputStreamOperator<Tuple3<String, Integer, String>> resultDS = textStream
                .connect(broadcastStream)
                .process(new BroadcastProcessFunction<Tuple2<String, Integer>, Tuple2<Integer, String>, Tuple3<String, Integer, String>>() {

                    // 广播流的数据和主流中的数据进行一个关联：用户ID,城市ID
                    @Override
                    public void processElement(Tuple2<String, Integer> value, BroadcastProcessFunction<Tuple2<String, Integer>, Tuple2<Integer, String>, Tuple3<String, Integer, String>>.ReadOnlyContext ctx, Collector<Tuple3<String, Integer, String>> out) throws Exception {
                        // 获取广播流中的数据
                        ReadOnlyBroadcastState<Integer, String> broadcastState = ctx.getBroadcastState(dimMapStateDescriptor);
                        String cityName = "";
                        if (broadcastState.contains(value.f1)) {
                            cityName = broadcastState.get(value.f1);
                            out.collect(new Tuple3<>(value.f0, value.f1, cityName));
                        }
                    }

                    // 处理广播流的数据
                    @Override
                    public void processBroadcastElement(Tuple2<Integer, String> value, BroadcastProcessFunction<Tuple2<String, Integer>, Tuple2<Integer, String>, Tuple3<String, Integer, String>>.Context ctx, Collector<Tuple3<String, Integer, String>> out) throws Exception {
                        System.out.println("收到这条广播数据： " + value + "," + getRuntimeContext().getIndexOfThisSubtask()); // 城市ID，城市数据
                        ctx.getBroadcastState(dimMapStateDescriptor).put(value.f0, value.f1);
                    }
                });

        resultDS.print();
        env.execute("JoinDemo4 >>> 2021-09-07 ");
    }
}
