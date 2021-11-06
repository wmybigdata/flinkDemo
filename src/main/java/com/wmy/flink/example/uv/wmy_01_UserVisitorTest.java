package com.wmy.flink.example.uv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Jedis;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.example
 * @Author: wmy
 * @Date: 2021/9/14
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Flink去重统计-基于自定义布隆过滤器
 * @Version: wmy-version-01
 */
public class wmy_01_UserVisitorTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setParallelism(1);

        DataStreamSource<String> kafkaSource = env.socketTextStream("flink04", 8888);


         /*//使用自定义的方式来进行指标的监控
        SingleOutputStreamOperator<String> result = kafkaSource
                .map(new RichMapFunction<String, String>() {
                    Counter mapDataNub;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        mapDataNub = getRuntimeContext()
                                .getMetricGroup()
                                .addGroup("wmy_counter")
                                .counter("mapDataNub");
                    }

                    String s1 = "";

                    @Override
                    public String map(String value) throws Exception {
                        try {
                            String[] fields = value.split(",");
                            long userID = Long.parseLong(fields[0]);
                            long productID = Long.parseLong(fields[1]);
                            long categoryID = Long.parseLong(fields[2]);
                            String type = fields[3];
                            long timestamp = Long.parseLong(fields[2]);

                            Map<String, Object> map = new HashMap<>();
                            map.put("userID", userID);
                            map.put("productID", productID);
                            map.put("categoryID", categoryID);
                            map.put("type", type);
                            map.put("timestamp", timestamp);
                            s1 = JSON.toJSONString(map);
                            mapDataNub.inc();
                            System.out.println("数据：" + map.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        return s1;
                    }
                });


        // 使用系统的方式来进行指标的监控
        SingleOutputStreamOperator<String> result1 = kafkaSource
                .map(new RichMapFunction<String, String>() {
                    private transient int valueToExpose = 0;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        getRuntimeContext()
                                .getMetricGroup()
                                .gauge("wmy-gauge", new Gauge<Integer>() {
                                    @Override
                                    public Integer getValue() {
                                        return valueToExpose;
                                    }
                                });
                    }

                    String s1 = "";

                    @Override
                    public String map(String value) throws Exception {
                        try {
                            String[] fields = value.split(",");
                            long userID = Long.parseLong(fields[0]);
                            long productID = Long.parseLong(fields[1]);
                            long categoryID = Long.parseLong(fields[2]);
                            String type = fields[3];
                            long timestamp = Long.parseLong(fields[2]);

                            valueToExpose++;
                            Map<String, Object> map = new HashMap<>();
                            map.put("userID", userID);
                            map.put("productID", productID);
                            map.put("categoryID", categoryID);
                            map.put("type", type);
                            map.put("timestamp", timestamp);
                            s1 = JSON.toJSONString(map);
                            System.out.println("数据：" + map.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        return s1;
                    }
                });


        SingleOutputStreamOperator<String> result2 = kafkaSource
                .map(new RichMapFunction<String, String>() {
                    private transient Histogram histogram;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        com.codahale.metrics.Histogram dropwizardHistogram =
                                new com.codahale.metrics.Histogram(new SlidingWindowReservoir(1));

                        this.histogram = getRuntimeContext()
                                .getMetricGroup()
                                .histogram("wmy-myHistogram", new DropwizardHistogramWrapper(dropwizardHistogram));
                    }

                    String s1 = "";

                    @Override
                    public String map(String value) throws Exception {
                        try {
                            String[] fields = value.split(",");
                            long userID = Long.parseLong(fields[0]);
                            long productID = Long.parseLong(fields[1]);
                            long categoryID = Long.parseLong(fields[2]);
                            String type = fields[3];
                            long timestamp = Long.parseLong(fields[2]);

                            Map<String, Object> map = new HashMap<>();
                            map.put("userID", userID);
                            map.put("productID", productID);
                            map.put("categoryID", categoryID);
                            map.put("type", type);
                            map.put("timestamp", timestamp);
                            s1 = JSON.toJSONString(map);

                            this.histogram.update(1);

                            System.out.println("数据：" + map.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        return s1;
                    }
                });*/

        // 指定时间语义
        WatermarkStrategy<UserBehavior> watermarkStrategy = WatermarkStrategy
                .<UserBehavior>forMonotonousTimestamps()
                .withTimestampAssigner(new SerializableTimestampAssigner<UserBehavior>() {
                    @Override
                    public long extractTimestamp(UserBehavior element, long recordTimestamp) {
                        return element.getTimestamp() * 1000L;
                    }
                });

        // 对kafka中的数据进行处理
        SingleOutputStreamOperator<UserBehavior> mapDS = kafkaSource
                .map(new MapFunction<String, UserBehavior>() {
                    @Override
                    public UserBehavior map(String value) throws Exception {
                        String[] fields = value.split(",");
                        long userID = Long.parseLong(fields[0]);
                        long productID = Long.parseLong(fields[1]);
                        long categoryID = Long.parseLong(fields[2]);
                        String type = fields[3];
                        long timestamp = Long.parseLong(fields[4]);

                        // 采用构造者模式来进行对象的编写
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
        SingleOutputStreamOperator<UserBehavior> filterDS = mapDS
                .filter(new FilterFunction<UserBehavior>() {
                    @Override
                    public boolean filter(UserBehavior value) throws Exception {
                        if (value.getType().equals("pv")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

        SingleOutputStreamOperator<UserBehavior> watermarkDS = filterDS.assignTimestampsAndWatermarks(watermarkStrategy);

        // 去重按全局去重，故使用行为分组，仅为后续开窗使用
        WindowedStream<UserBehavior, String, TimeWindow> windowDS = watermarkDS.keyBy(UserBehavior::getType)
                .window(TumblingEventTimeWindows.of(Time.seconds(2)));

        WindowedStream<UserBehavior, String, TimeWindow> triggerDS = windowDS.trigger(new MyTrigger());

        SingleOutputStreamOperator<UserVisitorCount> processDS = triggerDS.process(new UserVisitorWindowFunc());
        processDS.print();

        env.execute("wmy_01_UserVisitorTest");

    }

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class UserVisitorCount {
        private String type;
        private String windowEnd;
        private int count;
    }

    // 自定义一个触发器：来一条计算一条（访问Redis一次）
    private static class MyTrigger extends Trigger<UserBehavior,TimeWindow> {

        @Override
        public TriggerResult onElement(UserBehavior element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.FIRE_AND_PURGE; // 触发计算和清楚窗口元素
        }

        @Override
        public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
            return TriggerResult.CONTINUE;
        }

        @Override
        public void clear(TimeWindow window, TriggerContext ctx) throws Exception {

        }
    }

    // 定义一个全局的窗口函数来进行处理数据
    private static class UserVisitorWindowFunc extends ProcessWindowFunction<UserBehavior, UserVisitorCount, String, TimeWindow> {
        // 声明Redis连接
        private Jedis jedis;

        // 声明布隆过滤器
        private MyBloomFilter myBloomFilter;

        // 声明每个窗口总人数的key
        private String hourUVCountKey;

        @Override
        public void open(Configuration parameters) throws Exception {
            jedis = new Jedis("dm", 6379);
            hourUVCountKey = "HourUV";
            myBloomFilter = new MyBloomFilter(1 << 30);
        }

        @Override
        public void process(String s, ProcessWindowFunction<UserBehavior, UserVisitorCount, String, TimeWindow>.Context context, Iterable<UserBehavior> elements, Collector<UserVisitorCount> out) throws Exception {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //1.取出数据
            UserBehavior userBehavior = elements.iterator().next();
            //2.提取窗口信息
            String windowEnd = simpleDateFormat.format(new Date(context.window().getEnd()));
            //3.定义当前窗口的BitMap Key
            String bitMapKey = "BitMap_" + windowEnd;
            //4.查询当前的UID是否已经存在于当前的bitMap中
            long offset = myBloomFilter.getOffset(userBehavior.getUserID().toString());
            Boolean exists = jedis.getbit(bitMapKey, offset);

            //5.根据数据是否存在做下一步操作
            if (!exists) {
                //将对应offset位置改为1
                jedis.setbit(bitMapKey, offset, true);
                //累加当前窗口的综合
                jedis.hincrBy(hourUVCountKey, windowEnd, 1);
            }
            //输出数据
            String hget = jedis.hget(hourUVCountKey, windowEnd);
            out.collect(
                    UserVisitorCount
                            .builder()
                            .type("UV")
                            .windowEnd(windowEnd)
                            .count(Integer.parseInt(hget))
                            .build()
            );
        }
    }

    // 自定义布隆过滤器
    private static class MyBloomFilter {
        // 减少哈希冲突优化：增加了过滤器容量为数据3-10倍
        // 定义布隆过滤器容量，最好是传入2的整次幂数据
        private long capacity;

        public MyBloomFilter(long capacity) {
            this.capacity = capacity;
        }

        // 传入一个字符串，获取BitMap中的位置
        private long getOffset(String value) {
            long result = 0L;

            // 减少哈希冲突优化2，优化哈希算法
            // 对字符串的Unicode编码乘以要给质数31再相加
            for (char c : value.toCharArray()) {
                result += result * 31 + c;
            }

            // 取模：使用位与运算代替取模效率更高
            return result % (capacity - 1);
        }
    }
}
