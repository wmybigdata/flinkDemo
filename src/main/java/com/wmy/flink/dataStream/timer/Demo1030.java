package com.wmy.flink.dataStream.timer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.timer
 * @Author: wmy
 * @Date: 2021/11/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink定时器 Demo
 * @Version: wmy-version-01
 */
public class Demo1030 {
    public static void main(String[] args) throws Exception {
        System.out.println("Demo1030 >>> 2021-10-30");
        // 最后是整个逻辑功能的主体：Demo1030.java，这里面有自定义的KeyedProcessFunction子类，还有程序入口的main方法
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 并行度1
        env.setParallelism(1);

        // 处理时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

        // 监听本地8888端口，读取字符串
        DataStream<String> socketDataStream = env.socketTextStream("clickhouse01", 8888);

        socketDataStream.flatMap(new Splitter())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple2<String, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner(new SerializableTimestampAssigner<Tuple2<String, Integer>>() {
                                    @Override
                                    public long extractTimestamp(Tuple2<String, Integer> element, long recordTimestamp) {
                                        return System.currentTimeMillis();
                                    }
                                }))
                .keyBy(0)
                .process(new CountWithTimeoutFunction())
                .print();


        env.execute("Demo1030 >>> 2021-10-30");

    }

    static class CountWithTimeoutFunction extends KeyedProcessFunction<Tuple, Tuple2<String, Integer>, Tuple2<String, Long>> {

        // 自定义状态
        private ValueState<CountWithTimestamp> state;

        @Override
        public void open(Configuration parameters) throws Exception {
            // 初始化状态，name是myState
            state = getRuntimeContext().getState(new ValueStateDescriptor<>("myState", CountWithTimestamp.class));
        }

        @Override
        public void processElement(
                Tuple2<String, Integer> value,
                Context ctx,
                Collector<Tuple2<String, Long>> out) throws Exception {

            // 取得当前是哪个单词
            Tuple currentKey = ctx.getCurrentKey();

            // 从backend取得当前单词的myState状态
            CountWithTimestamp current = state.value();

            // 如果myState还从未没有赋值过，就在此初始化
            if (current == null) {
                current = new CountWithTimestamp();
                current.key = value.f0;
            }

            // 单词数量加一
            current.count++;

            // 取当前元素的时间戳，作为该单词最后一次出现的时间
            current.lastModified = ctx.timestamp();

            // 重新保存到backend，包括该单词出现的次数，以及最后一次出现的时间
            state.update(current);

            // 为当前单词创建定时器，十秒后后触发
            long timer = current.lastModified + 10000;

            ctx.timerService().registerProcessingTimeTimer(timer);

            // 打印所有信息，用于核对数据正确性
            System.out.println(String.format("process, %s, %d, lastModified : %d (%s), timer : %d (%s)\n\n",
                    currentKey.getField(0),
                    current.count,
                    current.lastModified,
                    time(current.lastModified),
                    timer,
                    time(timer)));

        }

        /**
         * 定时器触发后执行的方法
         * @param timestamp 这个时间戳代表的是该定时器的触发时间
         * @param ctx
         * @param out
         * @throws Exception
         */
        @Override
        public void onTimer(
                long timestamp,
                OnTimerContext ctx,
                Collector<Tuple2<String, Long>> out) throws Exception {

            // 取得当前单词
            Tuple currentKey = ctx.getCurrentKey();

            // 取得该单词的myState状态
            CountWithTimestamp result = state.value();

            // 当前元素是否已经连续10秒未出现的标志
            boolean isTimeout = false;

            // timestamp是定时器触发时间，如果等于最后一次更新时间+10秒，就表示这十秒内已经收到过该单词了，
            // 这种连续十秒没有出现的元素，被发送到下游算子
            if (timestamp == result.lastModified + 10000) {
                // 发送
                out.collect(new Tuple2<String, Long>(result.key, result.count));

                isTimeout = true;
            }

            // 打印数据，用于核对是否符合预期
            System.out.println(String.format("ontimer, %s, %d, lastModified : %d (%s), stamp : %d (%s), isTimeout : %s\n\n",
                    currentKey.getField(0),
                    result.count,
                    result.lastModified,
                    time(result.lastModified),
                    timestamp,
                    time(timestamp),
                    String.valueOf(isTimeout)));
        }
    }

    // 自定义的类
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountWithTimestamp {
        public String key;
        public Long count;
        public long lastModified;
    }

    // 创建FlatMapFunction的实现类Splitter，作用是将字符串分割后生成多个Tuple2实例，f0是分隔后的单词，f1等于1：
    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {

            if(StringUtils.isNullOrWhitespaceOnly(s)) {
                System.out.println("invalid line");
                return;
            }

            for(String word : s.split(",")) {
                collector.collect(new Tuple2<String, Integer>(word, 1));
            }
        }
    }

    public static String time(long timeStamp) {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(timeStamp));
    }
}
