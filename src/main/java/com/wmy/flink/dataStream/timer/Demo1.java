package com.wmy.flink.dataStream.timer;

import lombok.*;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.timer
 * @Author: wmy
 * @Date: 2021/8/30
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static OutputTag<ResultInfo> outputTag = new OutputTag<ResultInfo>("orderTimeout") {
    };

    public static void main(String[] args) throws Exception {

        // 输出打印标记
        System.out.println("wmy-version-01");

        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取socketTextStream里面的数据
        DataStreamSource<String> streamSource = env.socketTextStream("flink04", 9999);

        // 转换为JSON
        KeyedStream<OrderInfo, Long> orderInfoLongKeyedStream = streamSource
                .map(line -> {
                    String[] fields = line.split(",");
                    return new OrderInfo(Long.parseLong(fields[0]), fields[1], fields[2], Long.parseLong(fields[3]));
                }).assignTimestampsAndWatermarks(
                        WatermarkStrategy.<OrderInfo>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner(new SerializableTimestampAssigner<OrderInfo>() {
                                    @Override
                                    public long extractTimestamp(OrderInfo element, long recordTimestamp) {
                                        return element.getEventTime() * 1000L;
                                    }
                                })
                ).keyBy(OrderInfo::getOrderID);

        SingleOutputStreamOperator<ResultInfo> processDS = orderInfoLongKeyedStream.process(new KeyedProcessFunction<Long, OrderInfo, ResultInfo>() {

            private ValueState<Boolean> isPayFlag;
            private ValueState<Long> isTimerState;

            @Override
            public void open(Configuration parameters) throws Exception {
                isPayFlag = getRuntimeContext().getState(new ValueStateDescriptor<Boolean>("state", Boolean.class, false));
                isTimerState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timer", Long.class, 0L));
            }

            @Override
            public void processElement(OrderInfo value, KeyedProcessFunction<Long, OrderInfo, ResultInfo>.Context ctx, Collector<ResultInfo> out) throws Exception {
                Boolean flag = isPayFlag.value();
                Long timer = isTimerState.value();

                if (value.getTypeInfo().equals("create")) {
                    if (flag) {
                        out.collect(new ResultInfo(value.getOrderID(), value.getOrderID() + " ---> 已经支付 。。。"));
                        ctx.timerService().deleteEventTimeTimer(timer);
                        isPayFlag.clear();
                        isTimerState.clear();
                    } else {
                        long ts = value.getEventTime() * 1000L + 15 * 60 * 1000L;
                        ctx.timerService().registerEventTimeTimer(ts);
                        isTimerState.update(ts);
                    }
                } else if (value.getTypeInfo().equals("pay")) {
                    if (timer > 0) {
                        if (timer > value.getEventTime() * 1000L) {
                            out.collect(new ResultInfo(value.getOrderID(), value.getOrderID() + " ---> 已经支付 。。。"));
                        } else {
                            ctx.output(outputTag, new ResultInfo(value.getOrderID(), value.getOrderID() + " ---> 订单已经超时 。。。"));
                        }
                        isPayFlag.clear();
                        isTimerState.clear();
                    } else {
                        isPayFlag.update(true);
                        ctx.timerService().registerEventTimeTimer(value.getEventTime() * 1000L);
                        isTimerState.update(value.getEventTime() * 1000L);
                    }
                }
            }
            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<Long, OrderInfo, ResultInfo>.OnTimerContext ctx, Collector<ResultInfo> out) throws Exception {
                Boolean flag = isPayFlag.value();

                if (flag) {
                    out.collect(new ResultInfo(ctx.getCurrentKey(), ctx.getCurrentKey() + " ---> 已经支付，但是没有下单信息 。。。"));
                } else {
                    ctx.output(outputTag, new ResultInfo(ctx.getCurrentKey(), ctx.getCurrentKey() + " ---> 订单超时未支付，订单取消 "));
                }
                // 清楚定时器
                isPayFlag.clear();
                isTimerState.clear();
            }
        });

        processDS.print();
        processDS.getSideOutput(outputTag).print();

        // 执行程序
        env.execute("wmy-version-01");
    }


    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class OrderInfo {
        private Long orderID;
        private String typeInfo;
        private String payInfo;
        private Long eventTime;
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class ResultInfo {
        private Long orderID;
        private String message;
    }
}
