package com.wmy.flink.dataStream.laterness;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.RichProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.laterness
 * @Author: wmy
 * @Date: 2021/11/2
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 针对于延迟太多的数据-默认是丢弃
 * @Version: wmy-version-01
 */
public class Demo1 {
    private static final Long MAX_ALLOWED_UNORDERED_TIME = 10 * 1000L; // 设置延迟时间

    public static void main(String[] args) throws Exception {
        System.out.println("Demo1 >>> 2021/11/2");

        // 需求：
        // 使用滚动计时窗口，计算安放在北京西站各个监测点的红外测温仪，每隔3秒钟，所探测到的旅客的平均体温。
        // 针对于延迟太多的数据，Flink默认处理的方案是丢弃，也就是：窗口若已经关闭了，再进入该窗口内的数据不是触发执行

        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据
        DataStreamSource<String> socketTextStream = env.socketTextStream("clickhouse01", 8888);

        // 将流中的数据进行转换为对象
        SingleOutputStreamOperator<Raytek> mapDS = socketTextStream.map(new MapFunction<String, Raytek>() {
            @Override
            public Raytek map(String value) throws Exception {
                String[] fields = value.split(",");
                return Raytek.builder()
                        .id(fields[0])
                        .temperature(Double.parseDouble(fields[1]))
                        .name(fields[2])
                        .ts(Long.parseLong(fields[3]))
                        .location(fields[4])
                        .build();
            }
        });

        // mapDS分配水位线
        SingleOutputStreamOperator<Raytek> watermarksDS = mapDS.assignTimestampsAndWatermarks(new MyAssignerWithPeriodicWatermarks(MAX_ALLOWED_UNORDERED_TIME));

        // 将watermarksDS进行分组开窗
        SingleOutputStreamOperator<String> processDS = watermarksDS
                .keyBy(Raytek::getId)
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                .process(new MyProcessWindowFunction());

        // 将处理之后的结果给打印
        processDS.print();

        env.execute("Demo1 >>> 2021/11/2");

    }


    // 自定义Raytek实体类
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Raytek {
        private String id;
        private Double temperature;
        private String name;
        private Long ts;
        private String location;
    }

    // 自定义水位线
    public static class MyAssignerWithPeriodicWatermarks implements AssignerWithPeriodicWatermarks<Raytek> {

        private Long maxAllowedUnorderedTime; // 定义一个运行乱序的时间
        private Long maxTimeStamp = 0L; // 定义一个最大的时间

        public MyAssignerWithPeriodicWatermarks(Long maxAllowedUnorderedTime) {
            this.maxAllowedUnorderedTime = maxAllowedUnorderedTime;
        }

        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(maxTimeStamp - maxAllowedUnorderedTime);
        }

        @Override
        public long extractTimestamp(Raytek element, long recordTimestamp) {
            Long nowTimeTS = element.getTs() * 1000L; // 现在的时间
            maxTimeStamp = Math.max(maxTimeStamp, nowTimeTS); // 获取当前事件里面最大的时间戳
            long currentMarkTimestamp = getCurrentWatermark().getTimestamp(); // 获取当前的时间戳
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formatNow = simpleDateFormat.format(nowTimeTS);
            String formatMaxTime = simpleDateFormat.format(maxTimeStamp);
            String formatWaterMarkTime = simpleDateFormat.format(currentMarkTimestamp);
            String format = String.format("当前事件时间：{%s}，最大的时间为：{%s}，水位线为：{%s}", formatNow, formatMaxTime, formatWaterMarkTime);
            System.out.println(format);
            return nowTimeTS;
        }
    }

    // 自定义处理函数
    public static class MyProcessWindowFunction extends RichProcessWindowFunction<Raytek, String, String, TimeWindow> {

        @Override
        public void process(String s, ProcessWindowFunction<Raytek, String, String, TimeWindow>.Context context, Iterable<Raytek> elements, Collector<String> out) throws Exception {
            int size = 0; // 统计这个窗口有多少元素
            float totalTemperature = 0.0F; // 统计这个窗口的总体温
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Iterator<Raytek> iterator = elements.iterator();
            while (iterator.hasNext()) {
                size++;
                totalTemperature += iterator.next().getTemperature();
            }
            float avgTemperature = totalTemperature / size;
            String startTime = simpleDateFormat.format(context.window().getStart());
            String endTime = simpleDateFormat.format(context.window().getEnd());
            String result = String.format("当前事件平均体温：{%f}，窗口开始时间为：{%s}，窗口结束时间为：{%s}，数据元素为：{%d}", avgTemperature, startTime, endTime,elements);
            out.collect(result);
        }
    }
}
