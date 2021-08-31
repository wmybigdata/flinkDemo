package com.wmy.flink.dataStream.timer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import scala.math.Ordering;

import java.time.Duration;
import java.util.Properties;
import java.util.Timer;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.timer
 * @Author: wmy
 * @Date: 2021/8/31
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class CreateorPay {
    private static OutputTag<OrderResult> outputTag = new OutputTag<OrderResult>("order-result-time"){};
    public static void main(String[] args) throws Exception {

        System.out.println("订单超时检测 。。。");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStreamSource<String> source = env.socketTextStream("flink04", 9999);
        SingleOutputStreamOperator<OrderInfo> process = source
                .process(new ProcessFunction<String, OrderInfo>() {
                    @Override
                    public void processElement(String s, Context context, Collector<OrderInfo> out) throws Exception {
                        String[] fields = s.split(",");
                        //客户id，商品id，创建or下单，timestamp
                        out.collect(new OrderInfo(Long.parseLong(fields[0]), fields[1], fields[2], Long.parseLong(fields[3])));
                    }
                });

        KeyedStream<OrderInfo, Long> keyedStream = process.assignTimestampsAndWatermarks(
                        WatermarkStrategy.<OrderInfo>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                                .withTimestampAssigner(new SerializableTimestampAssigner<OrderInfo>() {
                                    @Override
                                    public long extractTimestamp(OrderInfo element, long recordTimestamp) {
                                        return element.getEventTime() * 1000L;
                                    }
                                })
                )
                .keyBy(OrderInfo::getOrderId);

        SingleOutputStreamOperator<OrderResult> processDS = keyedStream.process(new KeyedProcessFunction<Long, OrderInfo, OrderResult>() {

            private ValueState<Boolean> isPlayedState;
            private ValueState<Long> isTimerState;

            /***
             * 1、先来的create，然后
             * 1.1、判断是否pay了，如果没有 注册一个定时器，触发时间是当前业务时间+15分钟
             * 1.2、判断是否pay了，如果有  证明乱序了，pay先来的，pay来我给他10秒的迟到允许时间，这就涉及到pay来的时候注册了一个定时器，如果没定时器证明过期了
             * @param parameters
             * @throws Exception
             */
            @Override
            public void open(Configuration parameters) throws Exception {
                isPlayedState = getRuntimeContext().getState(new ValueStateDescriptor<Boolean>("isplayedstate", Boolean.class, false));
                isTimerState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("istimerstate", Long.class, 0L));
            }

            @Override
            public void processElement(OrderInfo value, KeyedProcessFunction<Long, OrderInfo, OrderResult>.Context ctx, Collector<OrderResult> out) throws Exception {
                Boolean isPlayed = isPlayedState.value();
                Long timerTS = isTimerState.value();

                if (value.getEventType().equals("create")) {
                    if (isPlayed) {
                        if (timerTS > 0) {
                            System.out.println(timerTS);
                            if (timerTS >= value.getEventTime() * 1000L) {
                                out.collect(new OrderResult(value.getOrderId(), " 支付成功 。。。"));
                                ctx.timerService().deleteEventTimeTimer(timerTS);
                                isPlayedState.clear();
                                isTimerState.clear();
                            } else {
                                out.collect(new OrderResult(value.getOrderId(), " 支付成功 ,但是订单事件延迟 。。。"));
                                ctx.timerService().deleteEventTimeTimer(timerTS);
                                isPlayedState.clear();
                                isTimerState.clear();
                            }
                        }
                    } else {
                        // 未付款
                        long ts = value.getEventTime() * 1000L + 15 * 60 * 1000L;
                        isTimerState.update(ts);
                        ctx.timerService().registerEventTimeTimer(ts);
                    }
                } else if (value.getEventType().equals("pay")) {
                    if (timerTS > 0) {
                        if (timerTS > value.eventTime * 1000L) {
                            out.collect(new OrderResult(value.getOrderId(), " 支付成功 。。。"));
                        } else {
                            ctx.output(outputTag, new OrderResult(value.getOrderId(), " 支付超时 。。。 "));
                        }
                        ctx.timerService().deleteEventTimeTimer(timerTS);
                        isPlayedState.clear();
                        isTimerState.clear();
                    } else {
                        isPlayedState.update(true);
                        ctx.timerService().registerEventTimeTimer(value.eventTime * 1000L);
                        isTimerState.update(value.eventTime * 1000L);
                        System.out.println(isTimerState.value());
                    }
                }

            }

            @Override
            public void onTimer(long timestamp, KeyedProcessFunction<Long, OrderInfo, OrderResult>.OnTimerContext ctx, Collector<OrderResult> out) throws Exception {
                Boolean isPlayed = isPlayedState.value();
                if (isPlayed) {
                    ctx.output(outputTag, new OrderResult(ctx.getCurrentKey(), "支付成功，下单事件未到达 。。。"));
                    isTimerState.clear();
                } else {
                    ctx.output(outputTag, new OrderResult(ctx.getCurrentKey(), "订单取消 。。。"));
                    isPlayedState.clear();
                    isTimerState.clear();
                }
            }
        });

        /**
         * 1、测试下单事件先来，且支付成功
         * 34729,create,,1558430842
         * 34729,pay,,1558430843
         *
         *
         * 2、测试下单事件先来，且支付超时
         * 34732,create,,1558430842
         * 34732,pay,,1558431802
         *
         *
         * 3、支付事件先来，下单事件在延迟时间内也到达
         * 34733,pay,,1558430842
         * 34733,create,,1558430843
         *
         * 4、支付事件先来，下单事件未到达
         * 34734,pay,,1558430842
         * 34735,create,,1558431802
         *
         * 5、支付事件先来，下单事件很长事件才到达
         * 34736,pay,,1558430842
         * 34736,create,,1558430844
         * 这个水位线会影响他触发
         */

        processDS.print();
        processDS.getSideOutput(outputTag).print();

        env.execute("Pay or Create");
    }

    @Data
    @AllArgsConstructor
    // 定义输入订单事件的样例类
    public static class OrderInfo {
        private Long orderId;
        private String eventType;
        private String txId;
        private Long eventTime;
    }

    // 定义输出结果样例类
    @Data
    @AllArgsConstructor
    public static   class OrderResult {
        private Long orderId;
        private String resultMsg;
    }
}
