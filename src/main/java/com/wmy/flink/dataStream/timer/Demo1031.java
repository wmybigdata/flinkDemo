package com.wmy.flink.dataStream.timer;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.timer
 * @Author: wmy
 * @Date: 2021/11/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink自定义触发器演示
 * @Version: wmy-version-01
 */
public class Demo1031 {
    /**
     * 测试数据：
     * fb_3,leslie
     * fb_1,jack
     * fb_3,john
     * fb_9,tom
     * fb_2,leon
     * fb_3,kate
     * fb_3,alice
     * fb_2,john
     * fb_9,tom
     * <p>
     * fb_3,leslie
     * fb_3,john
     * fb_3,kate
     * fb_3,alice
     */
    public static void main(String[] args) throws Exception {
        System.out.println("Demo1031 >>> 2021-11-01");

        // 业务：停放着摆渡车，用来将游客在送到故宫出口处
        // 摆渡车出发：
        // 1、一辆摆渡车最多能够载4人
        // 2、停留的时间到了，停留3秒钟
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        KeyedStream<User, String> keyedStream = env.socketTextStream("clickhouse01", 8888)
                .filter(value -> StringUtils.isNotBlank(value.trim()))
                .map(new MapFunction<String, User>() {
                    @Override
                    public User map(String value) throws Exception {
                        String[] fields = value.split(",");
                        return User.builder()
                                .no(fields[0])
                                .timestamp(Long.parseLong(fields[1]))
                                .build();
                    }
                })
                .assignTimestampsAndWatermarks(WatermarkStrategy.<User>forMonotonousTimestamps()
                        .withTimestampAssigner(new SerializableTimestampAssigner<User>() {
                            @Override
                            public long extractTimestamp(User element, long recordTimestamp) {
                                return element.getTimestamp() * 1000L;
                            }
                        }))
                .keyBy(User::getNo);

        keyedStream
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .trigger(new MyTrigger(4))
                .process(new ProcessWindowFunction<User, Object, String, TimeWindow>() {
                    @Override
                    public void process(String s, ProcessWindowFunction<User, Object, String, TimeWindow>.Context context, Iterable<User> elements, Collector<Object> out) throws Exception {
                        User next = elements.iterator().next();
                        out.collect(s+":"+next);
                    }
                })
                .print();

        env.execute("Demo1031 >>> 2021-11-01");
    }

    /**
     * 参考CountTrigger来进行编写
     * <T> – 此Trigger工作的元素类型。
     * <W> – 此Trigger可以在其上运行的Windows类型。
     */
    public static class MyTrigger extends Trigger<User, TimeWindow> {
        private Integer maxCount; // 计数标记
        private ValueStateDescriptor<Integer> valueStateDescriptor = new ValueStateDescriptor<Integer>("valueStateDescriptor", Integer.class);

        private final ReducingStateDescriptor<Integer> reducingStateDescriptor = new ReducingStateDescriptor<Integer>("reducingStateDescriptor", new ReduceFunction<Integer>() {
            @Override
            public Integer reduce(Integer value1, Integer value2) throws Exception {
                return value1 + value2;
            }
        }, Integer.class);

        ValueStateDescriptor aa = new ValueStateDescriptor<Tuple2<String, Integer>>("aa", TypeInformation.of(new TypeHint<Tuple2<String, Integer>>() {
        }));

        public MyTrigger(Integer maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public TriggerResult onElement(User element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
            ReducingState<Integer> partitionedState = ctx.getPartitionedState(reducingStateDescriptor);
            partitionedState.add(1);

            // 判断是否到达人数统计
            if (partitionedState.get() >= maxCount) {
                System.out.println("摆渡车计数出发，人数为：" + maxCount);
                partitionedState.clear();
                return TriggerResult.FIRE_AND_PURGE;
            } else {
                return TriggerResult.CONTINUE;
            }
        }

        @Override
        public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {

            if (window.maxTimestamp() <= ctx.getCurrentWatermark()) {
                return TriggerResult.FIRE_AND_PURGE;
            } else {
                ctx.registerProcessingTimeTimer(time + 30_1000L);
                return TriggerResult.CONTINUE;
            }
        }

        @Override
        public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            System.out.println("事件时间到了：" + window.maxTimestamp());
            return TriggerResult.FIRE_AND_PURGE;
        }

        @Override
        public boolean canMerge() {
            return true; // 触发器是否能进行合并
        }

        @Override
        public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
            // 清空：状态、计时（事件和处理时间）
            ctx.getPartitionedState(reducingStateDescriptor).clear();
            ctx.deleteEventTimeTimer(window.maxTimestamp()); // 将处理时间和事件时间给清空
            ctx.deleteProcessingTimeTimer(window.maxTimestamp());
        }
    }

    @Data
    @Builder
    public static class User {
        private String no;
        private Long timestamp;
    }
}
