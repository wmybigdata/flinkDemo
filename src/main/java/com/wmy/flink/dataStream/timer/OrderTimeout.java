package com.wmy.flink.dataStream.timer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
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
 * @Desription: 订单
 * @Version: wmy-version-01
 */
public class OrderTimeout {

    public static OutputTag<OrderResult> orderTimeoutOutputTag = new OutputTag<OrderResult>("orderTimeout") {
    };

    public static void main(String[] args) throws Exception {
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.setParallelism(1);

        KeyedStream<OrderEvent, Long> orderEventStream = env.socketTextStream("flink04", 9999)
                .map(data -> {
                    String[] fields = data.split(",");
                    return new OrderEvent(Long.parseLong(fields[0]), fields[1], fields[2], Long.parseLong(fields[3]));
                })
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<OrderEvent>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner(new SerializableTimestampAssigner<OrderEvent>() {
                                    @Override
                                    public long extractTimestamp(OrderEvent element, long recordTimestamp) {
                                        return element.getEventTime() * 1000L;
                                    }
                                })
                )
                .keyBy(OrderEvent::getOrderId);

        SingleOutputStreamOperator<OrderResult> orderResultStream = orderEventStream.process(new OrderPayMatch());
        orderResultStream.print("payed");
        orderResultStream.getSideOutput(orderTimeoutOutputTag).print("timeoutorder");
        env.execute("version01");
    }

    public static class OrderPayMatch extends KeyedProcessFunction<Long, OrderEvent, OrderResult> {
        private ValueState<Boolean> isPayedState;
        private ValueState<Long> timerState;

        @Override
        public void open(Configuration parameters) throws Exception {
            isPayedState = getRuntimeContext().getState(new ValueStateDescriptor<Boolean>("state", Boolean.class,false));
            timerState = getRuntimeContext().getState(new ValueStateDescriptor<Long>("timer", Long.class,0L));
        }

        @Override

        public void processElement(OrderEvent value, KeyedProcessFunction<Long, OrderEvent, OrderResult>.Context ctx, Collector<OrderResult> out) throws Exception {
            Boolean isPayed = isPayedState.value();
            Long timerTs = timerState.value();

            if (value.eventType.equals("create")) {
                // 乱序行为，先得到pay，再得到create
                if (isPayed) {
                    out.collect(new OrderResult(value.getOrderId(), "payed successfully"));
                    ctx.timerService().deleteEventTimeTimer(timerTs);
                    isPayedState.clear();
                    timerState.clear();
                } else {
                    // 已经创建订单未支付，设置定时器
                    long ts = value.getEventTime() * 1000L + 15 * 60 * 1000L;
                    ctx.timerService().registerEventTimeTimer(ts);
                    timerState.update(ts);
                }
            } else if (value.eventType.equals("pay")) {
                //假如有定时器，说明create过
                if (timerTs > 0) {
                    // timerTs 是 认为超时后的时间戳
                    if (timerTs > value.getEventTime() * 1000L) {
                        out.collect(new OrderResult(value.getOrderId(), "payed successfully"));
                    } else {
                        ctx.output(orderTimeoutOutputTag, new OrderResult(value.getOrderId(), "this order is timeout"));
                    }
                    ctx.timerService().deleteEventTimeTimer(timerTs);
                    isPayedState.clear();
                    timerState.clear();
                } else {
                    // 先来pay
                    isPayedState.update(true);
                    // 等待watermark时间
                    ctx.timerService().registerEventTimeTimer(value.getEventTime() * 1000L);
                    timerState.update(value.getEventTime() * 1000L);
                }
            }
        }

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<Long, OrderEvent, OrderResult>.OnTimerContext ctx, Collector<OrderResult> out) throws Exception {
            Boolean isPayed = isPayedState.value();
            if (isPayed) {
                ctx.output(orderTimeoutOutputTag, new OrderResult(ctx.getCurrentKey(), "payed but no create ..."));
            } else {
                // 典型案例，只有create，没有pay
                ctx.output(orderTimeoutOutputTag, new OrderResult(ctx.getCurrentKey() , "order timeout ... "));
            }
            // 清楚定时器
            isPayedState.clear();
            timerState.clear();
        }
    }

    @Data
    @AllArgsConstructor
    // 定义输入订单事件的样例类
    public static class OrderEvent {
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
