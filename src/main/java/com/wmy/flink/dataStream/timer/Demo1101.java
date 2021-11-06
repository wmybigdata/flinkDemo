package com.wmy.flink.dataStream.timer;

import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.timer
 * @Author: wmy
 * @Date: 2021/11/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink 触发器
 * @Version: wmy-version-01
 */
public class Demo1101 {
    public static void main(String[] args) throws Exception {
        System.out.println("Demo1101 >>> 2021-11-01");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        DataStreamSource<String> source = env.socketTextStream("clickhouse01", 8888);

        SingleOutputStreamOperator<Tuple2<String, Long>> process = source.process(new ProcessFunction<String, Tuple2<String, Long>>() {
            @Override
            public void processElement(String value, Context ctx, Collector<Tuple2<String, Long>> out) throws Exception {
                String[] split = value.split(",", -1);
                out.collect(Tuple2.of(split[0], new Long(split[1])));
            }
        });

        WatermarkStrategy<Tuple2<String, Long>> tuple2WatermarkStrategy = WatermarkStrategy
                .<Tuple2<String, Long>>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Long>>() {
                    @Override
                    public long extractTimestamp(Tuple2<String, Long> element, long recordTimestamp) {
                        return element.f1;
                    }
                });
        //.withIdleness(Duration.ofSeconds(10));

        SingleOutputStreamOperator<Tuple2<String, Long>> tuple2SingleOutputStreamOperator = process.assignTimestampsAndWatermarks(tuple2WatermarkStrategy);

        SingleOutputStreamOperator<String> processDS = tuple2SingleOutputStreamOperator
                .keyBy(x -> x.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .trigger(new Trigger<Tuple2<String, Long>, TimeWindow>() {
                    private static final long serialVersionUID = 1L;
                    ValueState<Boolean> hello;
                    ValueState<Long> protime;

                    @Override
                    public TriggerResult onElement(Tuple2<String, Long> element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
                        hello = ctx.getPartitionedState(new ValueStateDescriptor<Boolean>("Hello", Types.BOOLEAN, false));
                        protime = ctx.getPartitionedState(new ValueStateDescriptor<Long>("Protime", Types.LONG, 0L));
                        System.out.println(hello.value());

                        if (window.maxTimestamp() <= ctx.getCurrentWatermark()) {
                            System.out.println("82");
                            return TriggerResult.FIRE_AND_PURGE;
                        } else {
                            if (!hello.value()) {
                                hello.update(true);
                                ctx.registerEventTimeTimer(timestamp + 20_000L);
                                protime.update(timestamp + 20_000L);
                            }
                            ctx.registerEventTimeTimer(window.maxTimestamp());
                            return TriggerResult.CONTINUE;
                        }

                    }

                    @Override
                    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                        System.out.println("onProcessingTime:" + ctx.getCurrentProcessingTime());
                        return TriggerResult.FIRE;
                    }

                    @Override
                    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
                        System.out.println("time:" + time);
                        System.out.println("window.maxTimestamp():" + window.maxTimestamp());
                        if (time == window.maxTimestamp()) {
                            ctx.deleteEventTimeTimer(protime.value());
                            return TriggerResult.FIRE_AND_PURGE;
                        } else {
                            return TriggerResult.CONTINUE;
                        }
                    }

                    @Override
                    public void clear(TimeWindow window, TriggerContext ctx) throws Exception {
                        System.out.println("clear");
                        System.out.println("protime.value():"+protime.value());
                        ctx.deleteEventTimeTimer(window.maxTimestamp());
                        ctx.deleteProcessingTimeTimer(protime.value());
                        hello.clear();
                        protime.clear();
                    }

                    @Override
                    public boolean canMerge() {
                        return true;
                    }

                    @Override
                    public void onMerge(TimeWindow window, OnMergeContext ctx) throws Exception {
                        long windowMaxTimestamp = window.maxTimestamp();
                        if (windowMaxTimestamp > ctx.getCurrentWatermark()) {
                            ctx.registerEventTimeTimer(windowMaxTimestamp);
                        }
                    }
                }).process(new ProcessWindowFunction<Tuple2<String, Long>, String, String, TimeWindow>() {
                    @Override
                    public void process(String s, ProcessWindowFunction<Tuple2<String, Long>, String, String, TimeWindow>.Context context, Iterable<Tuple2<String, Long>> elements, Collector<String> out) throws Exception {
                        Iterator<Tuple2<String, Long>> iterator = elements.iterator();
                        ArrayList<Tuple2<String, Long>> tuple2s = Lists.newArrayList(iterator);
                        out.collect(s + "\t:" + tuple2s.size());
                    }
                });

        processDS.print();

        env.execute("Demo1101 >>> 2021-11-01");
    }
}
