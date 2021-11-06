package com.wmy.flink.dataStream.join;

import com.google.common.cache.*;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.join
 * @Author: wmy
 * @Date: 2021/9/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 使用cache来减轻访问压力
 * @Version: wmy-version-01
 */
public class JoinDemo2 {
    public static void main(String[] args) throws Exception {
        System.out.println("JoinDemo1 ---> 20210907");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        SingleOutputStreamOperator<Tuple2<String, Integer>> textStream = env.socketTextStream("flink04", 8888, "\n")
                .map(line -> {
                    String[] fields = line.split(",");
                    return new Tuple2<String, Integer>(fields[0], Integer.valueOf(fields[1]));
                }).returns(new TypeHint<Tuple2<String, Integer>>() {
                });
        SingleOutputStreamOperator<Tuple3<String, Integer, String>> streamOperator = textStream.map(new MapJoinDemo1());
        streamOperator.print();
        env.execute("JoinDemo1");
    }

    /**
     * 可以使用缓存来存储一部分常访问的维表数据，以减少访问外部系统的次数，比如使用guava Cache。
     */
    public static class MapJoinDemo1 extends RichMapFunction<Tuple2<String, Integer>, Tuple3<String, Integer, String>> {

        LoadingCache<Integer, String> dimCache;

        @Override

        public void open(Configuration parameters) throws Exception {
            //使用google LoadingCache来进行缓存
            dimCache = CacheBuilder.newBuilder()
                    //最多缓存个数，超过了就根据最近最少使用算法来移除缓存
                    .maximumSize(1000)
                    //在更新后的指定时间后就回收
                    .expireAfterWrite(10, TimeUnit.MINUTES)
                    //指定移除通知
                    .removalListener(new RemovalListener<Integer, String>() {
                        @Override
                        public void onRemoval(RemovalNotification<Integer, String> removalNotification) {
                            System.out.println(removalNotification.getKey() + "被移除了，值为：" + removalNotification.getValue());
                        }
                    })
                    //指定加载缓存的逻辑
                    .build(new CacheLoader<Integer, String>() {
                        @Override
                        public String load(Integer key) throws Exception {
                            String cityName = readFromHbase(key);
                            return cityName;
                        }
                    });
        }

        private String readFromHbase(Integer cityID) {
            // 读取hbase
            // 这里模拟从hbase读取数据
            HashMap<Integer, String> hashMap = new HashMap<>();
            hashMap.put(1001, "beijing");
            hashMap.put(1002, "shanghai");
            hashMap.put(1003, "wuhan");
            hashMap.put(1004, "changsha");
            String cityName = "";
            if (hashMap.containsKey(cityID)) {
                cityName = hashMap.get(cityID);
            }
            return cityName;
        }

        @Override
        public Tuple3<String, Integer, String> map(Tuple2<String, Integer> value) throws Exception {
            // 在map方法中进行主流和维表的关联
            String cityName = "";
            if (dimCache.get(value.f1) != null) {
                cityName = dimCache.get(value.f1);
            }
            return new Tuple3<>(value.f0, value.f1, cityName);
        }
    }
}
