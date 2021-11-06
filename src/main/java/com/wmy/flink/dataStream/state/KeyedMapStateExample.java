package com.wmy.flink.dataStream.state;

import com.google.common.collect.Lists;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.state
 * @Author: wmy
 * @Date: 2021/9/29
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 测试MapState案例演示
 * @Version: wmy-version-01
 */
public class KeyedMapStateExample {
    public static void main(String[] args) throws Exception {
        //获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        //设置并行度
        env.setParallelism(16);

        //获取数据源
        DataStreamSource<Tuple2<Long, Long>> dataStreamSource =
                env.fromElements(
                        Tuple2.of(1L, 3L),
                        Tuple2.of(1L, 7L),
                        Tuple2.of(2L, 4L),
                        Tuple2.of(1L, 5L),
                        Tuple2.of(2L, 2L),
                        Tuple2.of(2L, 6L));

        // 输出：
        //(1,5.0)
        //(2,4.0)
        dataStreamSource
                .keyBy(0)
                .flatMap(new CountAverageWithMapState())
                .print();

        env.execute("KeyedMapStateExample");
    }

    /**
     * ValueState 这个状态为每个key保存一个值
     * value() 获取状态值
     * update() 更新状态值
     * clear() 清除状态
     */
    public static class CountAverageWithMapState extends RichFlatMapFunction<Tuple2<Long, Long>, Tuple2<Long, Double>> {

        private MapState<String, Long> mapState;

        @Override
        public void open(Configuration parameters) throws Exception {
            mapState = getRuntimeContext().getMapState(new MapStateDescriptor<String, Long>("mapState", String.class, Long.class));
        }

        @Override
        public void flatMap(Tuple2<Long, Long> value, Collector<Tuple2<Long, Double>> out) throws Exception {
            // 获取状态
            mapState.put(UUID.randomUUID().toString(), value.f1);
            ArrayList<Long> arrayList = Lists.newArrayList(mapState.values());

            if (arrayList.size() >= 3) {
                long count = 0;
                long sum = 0;
                for (Long aLong : arrayList) {
                    count++;
                    sum += aLong;
                }
                double avg = (double) sum / count;
                out.collect(Tuple2.of(value.f0, avg));
                mapState.clear();
            }
        }
    }
}
