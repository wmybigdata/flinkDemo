package com.wmy.flink.dataStream.watermark;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.guava18.com.google.common.collect.Iterators;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.watermark
 * @Author: wmy
 * @Date: 2021/11/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 无序流的案例演示
 * @Version: wmy-version-01
 */
public class UnOrderWatermarkDemo {
    private static final Long MAX_ALLOWED_UNORDERED_TIME = 10 * 1000L; // 设置延迟时间

    public static void main(String[] args) throws Exception {

        System.out.println("UnOrderWatermarkDemo >>> 2021-11-01");

        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        // 读取数据
        DataStreamSource<String> socketTextStream = env.socketTextStream("clickhouse01", 8888);

        // 将流转换为Raytek对象
        SingleOutputStreamOperator<Raytek> watermarkDS = socketTextStream
                .filter(value -> StringUtils.isNotBlank(value.trim()))
                .map(new MapFunction<String, Raytek>() {
                    @Override
                    public Raytek map(String value) throws Exception {
                        String[] fields = value.split(",");
                        return Raytek
                                .builder()
                                .id(fields[0])
                                .temperature(Double.parseDouble(fields[1]))
                                .name(fields[2])
                                .ts(Long.parseLong(fields[3]))
                                .location(fields[4])
                                .build();
                    }
                })
                .assignTimestampsAndWatermarks(new MyAssignerWithPeriodicWatermarks(MAX_ALLOWED_UNORDERED_TIME));

        SingleOutputStreamOperator<String> applyDS = watermarkDS
                .keyBy(Raytek::getId)
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                .apply(new MyRichWindowFunction());

        applyDS.print();

        env.execute("UnOrderWatermarkDemo >>> 2021-11-01");
    }

    // 自定义的分配水位线
    public static class MyAssignerWithPeriodicWatermarks implements AssignerWithPeriodicWatermarks<Raytek> {

        private Long maxAllowedUnorderedTime;

        public MyAssignerWithPeriodicWatermarks(Long maxAllowedUnorderedTime) {
            this.maxAllowedUnorderedTime = maxAllowedUnorderedTime;
        }

        private Long maxTimestamp = 0L; // 待分析的窗口中迄今为止最大的时间戳
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // 准备用于时间格式化的SimpleDateFormat实例


        @Nullable
        @Override
        public Watermark getCurrentWatermark() {
            return new Watermark(maxTimestamp - maxAllowedUnorderedTime); // 返回当前的Watermark
        }

        @Override
        public long extractTimestamp(Raytek element, long recordTimestamp) {
            Long nowTime = element.getTs() * 1000L; // 抽取出当前的时间戳
            maxTimestamp = Math.max(maxTimestamp, nowTime); // 获取当前的最大时间

            String nowTimeFormat = simpleDateFormat.format(nowTime); // 将当前的时间进行格式化
            String maxTimeStampFormat = simpleDateFormat.format(maxTimestamp); // 将最大的时间进行格式化
            String watermarkFormat = simpleDateFormat.format(getCurrentWatermark().getTimestamp()); // 将水位线时间进行格式化
            System.out.println("当前待分析的元素的时间戳是：" + nowTimeFormat + "。当前元素所在的窗口迄今位置最大的时间戳是：" + maxTimeStampFormat + "。当前元素携带的watermark中封装的时间戳是：" + watermarkFormat);
            return nowTime;
        }
    }

    public static class MyRichWindowFunction extends RichWindowFunction<Raytek, String, String, TimeWindow> {

        @Override
        public void apply(String key, TimeWindow window, Iterable<Raytek> input, Collector<String> out) throws Exception {
            int size = 0;
            float totalTmp = 0.0F;
            Iterator<Raytek> iterator = input.iterator();
            while (iterator.hasNext()) {
                totalTmp += iterator.next().getTemperature();
                size = size + 1;
            }
            float avgTmp = totalTmp / size;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            long start = window.getStart();
            long end = window.getEnd();
            String startFormat = simpleDateFormat.format(new Date(start));
            String endFormat = simpleDateFormat.format(new Date(end));
            String result = String.format("红外测温仪→{%s}，最近3秒内，测量到的旅客的平均体温是→{%f}，窗口的开始时间是→{%s}，窗口的结束时间是→{%s}", key, avgTmp, startFormat, endFormat);
            out.collect(result);
        }
    }
}