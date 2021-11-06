package com.wmy.flink.dataStream.broadcast;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.broadcast
 * @Author: wmy
 * @Date: 2021/11/2
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink 广播流演示
 * @Version: wmy-version-01
 */
public class Demo11021412 {
    // 1、在socketStream实时获得学生信息，对应的DataStream
    // 2、获得性别信息
    // 3、将数据量少的封装成广播无界流
    // 4、将广播无界流与 socket 学生信息进行connect连接，转换成connectedStream
    // 5、对connectedStream进行计算
    // 6、实时显示完整的学生信息
    // 7、启动Flink无界流应用

    // 测试数据

    // 1001,男
    // 1002,女

    // 1001,吴明洋,1001,杭州
    // 1002,林虎,1001,杭州
    // 1003,唐小敏,1002,杭州
    public static void main(String[] args) throws Exception {
        System.out.println("Demo11021412 >>> version-01");

        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据
        DataStreamSource<String> socketTextStream01 = env.socketTextStream("clickhouse01", 8888); // 性别
        DataStreamSource<String> socketTextStream02 = env.socketTextStream("clickhouse01", 9999); // 学生信息

        // 流转换
        SingleOutputStreamOperator<Tuple2<Integer, String>> mapDS01 = socketTextStream01.map(new MapFunction<String, Tuple2<Integer, String>>() {
            @Override
            public Tuple2<Integer, String> map(String value) throws Exception {
                String[] fields = value.split(",");
                return new Tuple2<>(Integer.parseInt(fields[0]), fields[1].trim()); // 编号、性别
            }
        });
        SingleOutputStreamOperator<Tuple4<Integer, String, Integer, String>> mapDS02 = socketTextStream02.map(new MapFunction<String, Tuple4<Integer, String, Integer, String>>() {
            @Override
            public Tuple4<Integer, String, Integer, String> map(String value) throws Exception {
                String[] fields = value.split(",");
                return new Tuple4<Integer, String, Integer, String>(Integer.parseInt(fields[0]), fields[1].trim(), Integer.parseInt(fields[2]), fields[3].trim()); // 编号、姓名、性别、地址
            }
        });

        // 创建广播状态描述器
        MapStateDescriptor<Integer, String> mapStateDescriptor = new MapStateDescriptor<>("mapDS01", new IntSerializer(), new StringSerializer());

        // 将mapDS01变成广播流
        BroadcastStream<Tuple2<Integer, String>> broadcastDS01 = mapDS01.broadcast(mapStateDescriptor);

        // 将mapDS02和broadcastDS01连接在一起
        SingleOutputStreamOperator<Tuple4<Integer, String, String, String>> processBroadCastDS = mapDS02
                .connect(broadcastDS01)
                // 非广播状态、广播状态、输出的类型
                .process(new BroadcastProcessFunction<Tuple4<Integer, String, Integer, String>, Tuple2<Integer, String>, Tuple4<Integer, String, String, String>>() {

                    @Override
                    public void processElement(Tuple4<Integer, String, Integer, String> value, BroadcastProcessFunction<Tuple4<Integer, String, Integer, String>, Tuple2<Integer, String>, Tuple4<Integer, String, String, String>>.ReadOnlyContext ctx, Collector<Tuple4<Integer, String, String, String>> out) throws Exception {
                        String genderInfo = ctx.getBroadcastState(mapStateDescriptor).get(value.f2); // 从刚才put进行的变量中获取数据
                        out.collect(new Tuple4<>(value.f0, value.f1, genderInfo, value.f3));
                    }

                    @Override
                    public void processBroadcastElement(Tuple2<Integer, String> value, BroadcastProcessFunction<Tuple4<Integer, String, Integer, String>, Tuple2<Integer, String>, Tuple4<Integer, String, String, String>>.Context ctx, Collector<Tuple4<Integer, String, String, String>> out) throws Exception {
                        // 流中元素的信息、性别，封装到Broadcast状态中
                        ctx.getBroadcastState(mapStateDescriptor).put(value.f0, value.f1); // 编号、性别
                    }
                });

        // 将处理之后的数据给输出出去
        processBroadCastDS.print();

        env.execute("Demo11021412 >>> version-01");
    }
}
