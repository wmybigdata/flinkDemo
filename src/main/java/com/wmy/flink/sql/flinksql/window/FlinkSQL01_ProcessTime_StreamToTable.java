package com.wmy.flink.sql.flinksql.window;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.sql.flinksql.window
 * @Author: wmy
 * @Date: 2021/9/24
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: FlinkSQL时间语义
 * @Version: wmy-version-01
 */
public class FlinkSQL01_ProcessTime_StreamToTable {
    public static void main(String[] args) {
        System.out.println("FlinkSQL01_ProcessTime_StreamToTable");

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        // 读取文本数据并转换为JavaBean对象
        SingleOutputStreamOperator<WaterSensor> waterSensorDS = env
                .readTextFile("src/main/resources/03-sensor.txt")
                .map(new MapFunction<String, WaterSensor>() {
                    @Override
                    public WaterSensor map(String value) throws Exception {
                        String[] fileds = value.split(",");
                        return WaterSensor.builder()
                                .id(fileds[0])
                                .ts(Long.parseLong(fileds[1]))
                                .vc(Integer.parseInt(fileds[2]))
                                .build();
                    }
                });

        // 将流转换为表，并指定处理时间
        Table table = tableEnv.fromDataStream(
                waterSensorDS,
                $("id"),
                $("ts"),
                $("vc"),
                $("pt").proctime() // 这个字段也是可以在DDL的语句里面进行使用的：PROCTIME()
        );

        // 打印元数据信息
        /**
         * (
         *   `id` STRING,
         *   `ts` BIGINT,
         *   `vc` INT,
         *   `pt` TIMESTAMP_LTZ(3) *PROCTIME*
         * )
         */
        table.printSchema();

        // 时间的指定：https://nightlies.apache.org/flink/flink-docs-release-1.13/docs/dev/table/concepts/time_attributes/

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WaterSensor {
        private String id;
        private Long ts;
        private Integer vc;
    }
}
