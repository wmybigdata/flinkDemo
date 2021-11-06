package com.wmy.flink.example.topN;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.example.topN
 * @Author: wmy
 * @Date: 2021/9/16
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 每隔5分钟输出最近1小时内点击量最多的前N个商品
 * @Version: wmy-version-01
 */
public class ProductTopN {
    public static void main(String[] args) throws Exception {
        // 打印版本信息
        System.out.println("wmy-flink-topn-2021-09-16");

        // 创建环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        SingleOutputStreamOperator<UserBehavior> socketStream = env.socketTextStream("flink01", 8888)
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String value) throws Exception {
                        String[] fields = value.split(",");
                        long userID = Long.parseLong(fields[0]);
                        long productID = Long.parseLong(fields[1]);
                        long categoryID = Long.parseLong(fields[2]);
                        String type = fields[3];
                        long timestamp = Long.parseLong(fields[4]);
                        return UserBehavior
                                .builder()
                                .userID(userID)
                                .productID(productID)
                                .categoryID(categoryID)
                                .type(type)
                                .timestamp(timestamp)
                                .build();
                    }
                });

        // 创建时间戳水印，假设不是乱序的
        WatermarkStrategy<UserBehavior> watermarkStrategy = WatermarkStrategy
                .<UserBehavior>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                    @Override
                    public long extractTimestamp(UserBehavior element, long recordTimestamp) {
                        return element.getTimestamp() * 1000L;
                    }
                });

        // 具有时间戳水印的流
        SingleOutputStreamOperator<UserBehavior> watermarkDS = socketStream
                .filter(new FilterFunction<UserBehavior>() {
                    @Override
                    public boolean filter(UserBehavior value) throws Exception {
                        if (value.getType().equals("pv")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }).assignTimestampsAndWatermarks(watermarkStrategy);

        // 通过productID进行分组，转换成Tuple形式，进行开窗然后在进行局部累加最后全窗口输出
        SingleOutputStreamOperator<ItemCount> aggregateDS = watermarkDS
                .map(new MapFunction<UserBehavior, Tuple2<Long, Integer>>() {
                    @Override
                    public Tuple2<Long, Integer> map(UserBehavior value) throws Exception {
                        return new Tuple2<>(value.getProductID(), 1);
                    }
                })
                .keyBy(userBehavior -> userBehavior.f0)
                .window(SlidingEventTimeWindows.of(Time.hours(1), Time.minutes(5)))
                .aggregate(new ItemCountAggFunc(), new ItemCountWindowFunc());


        // 对聚合之后的ItemCount进行排序
        SingleOutputStreamOperator<String> productTopNDS = aggregateDS
                .keyBy(ItemCount::getWindowEnd)
                .process(new ItemCountProcessFunc(3));

        productTopNDS.print();

        // 执行环境
        env.execute("product-topn");

    }

    // 用户行为表
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class UserBehavior {
        private Long userID;
        private Long productID;
        private Long categoryID;
        private String type;
        private Long timestamp;
    }

    // 聚合之后的结果
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ItemCount {
        private Long productID;
        private String windowEnd;
        private Integer element;
    }

    // 创建一次聚合操作
    private static class ItemCountAggFunc implements AggregateFunction<Tuple2<Long, Integer>, Integer, Integer> {

        @Override
        public Integer createAccumulator() {
            return 0;
        }

        @Override
        public Integer add(Tuple2<Long, Integer> value, Integer accumulator) {
            return accumulator + 1;
        }

        @Override
        public Integer getResult(Integer accumulator) {
            return accumulator;
        }

        @Override
        public Integer merge(Integer a, Integer b) {
            return a + b;
        }
    }

    // 进行一次全窗口的输出
    private static class ItemCountWindowFunc implements WindowFunction<Integer, ItemCount, Long, TimeWindow> {

        @Override
        public void apply(Long key, TimeWindow window, Iterable<Integer> input, Collector<ItemCount> out) throws Exception {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String windowEnd = simpleDateFormat.format(new Date(window.getEnd()));
            Integer element = input.iterator().next();
            out.collect(
                    ItemCount
                            .builder()
                            .productID(key)
                            .windowEnd(windowEnd)
                            .element(element)
                            .build()
            );
        }
    }

    // 对窗口中的元素进行合并
    private static class ItemCountProcessFunc extends KeyedProcessFunction<String, ItemCount, String> {

        // 定义构造器可以按入参排名
        private Integer topN;

        public ItemCountProcessFunc(Integer topN) {
            this.topN = topN;
        }

        // 使用ListState并初始化
        private ListState<ItemCount> listState;

        @Override
        public void open(Configuration parameters) throws Exception {
            listState = getRuntimeContext().getListState(new ListStateDescriptor<ItemCount>("listState", ItemCount.class));
        }

        // 定时器
        @Override
        public void processElement(ItemCount value, KeyedProcessFunction<String, ItemCount, String>.Context ctx, Collector<String> out) throws Exception {
            listState.add(value);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ctx.timerService().registerEventTimeTimer(simpleDateFormat.parse(value.getWindowEnd()).getTime() + 1000L);
        }

        @Override
        public void onTimer(long timestamp, KeyedProcessFunction<String, ItemCount, String>.OnTimerContext ctx, Collector<String> out) throws Exception {
            // 1、开始获取状态的数据并转为List
            Iterator<ItemCount> itemCountIterator = listState.get().iterator();

            // 2、排序
            ArrayList<ItemCount> itemCounts = Lists.newArrayList(itemCountIterator);
            itemCounts.sort(new Comparator<ItemCount>() {
                @Override
                public int compare(ItemCount o1, ItemCount o2) {
                    if (o2.getElement() < o1.getElement()) {
                        return -1;
                    } else if (o2.getElement() > o1.getElement()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            // 获取topN
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer
                    .append("=============================")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("=============================")
                    .append("\n");

            for (int i = 0; i < Math.min(topN, itemCounts.size()); i++) {
                ItemCount itemCount = itemCounts.get(i);
                stringBuffer
                        .append("Top").append(i + 1)
                        .append("   ItemID: ").append(itemCount.getProductID())
                        .append("   Counts: ").append(itemCount.getElement())
                        .append("\n");
            }

            stringBuffer
                    .append("=============================")
                    .append(new Timestamp(timestamp - 1000L))
                    .append("=============================")
                    .append("\n")
                    .append("\n");
            listState.clear();
            out.collect(stringBuffer.toString());
            //Thread.sleep(20); // 方便查看结果时间休眠
        }
    }

}
