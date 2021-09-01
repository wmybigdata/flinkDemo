package com.wmy.java.util.design;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 构造者设计模式
 * @Version: wmy-version-01
 */
public class ConstructorDesign {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> socketTextStream = env.socketTextStream("192.168.21.113", 8888);
        SingleOutputStreamOperator<User> streamOperator = socketTextStream.map(line -> {
            String[] fields = line.split(",");
            // 测试编写的效果
            //return new User(fields[0], fields[1], Integer.parseInt(fields[2]), fields[3]);
            return User
                    .builder()
                    .name("abc")
                    .build();
        });
        streamOperator.print();
        streamOperator.addSink(ClickHouseUtil.<User>getSinkFunc("insert into user values(?,?,?)"));
        env.execute();
    }

    public static class ClickHouseUtil {
        public static <T> SinkFunction<T> getSinkFunc(String sql) {

            return JdbcSink.sink(sql,
                    new JdbcStatementBuilder<T>() {
                        @Override
                        public void accept(PreparedStatement preparedStatement, T t) throws SQLException {

                            //通过反射的方式获取数据中的内容
                            //1.获取所有的列信息(包含私有属性)
                            Field[] declaredFields = t.getClass().getDeclaredFields();

                            //2.遍历列信息
                            int offset = 0;
                            for (int i = 0; i < declaredFields.length; i++) {
                                Field field = declaredFields[i];

                                //设置私有属性信息可获取
                                field.setAccessible(true);

                                //获取字段上的注解信息
                                TransientSink transientSink = field.getAnnotation(TransientSink.class);

                                if (transientSink != null) {
                                    offset++;
                                    continue;
                                }

                                try {
                                    Object o = field.get(t);
                                    preparedStatement.setObject(i + 1 - offset, o);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }, new JdbcExecutionOptions
                            .Builder()
                            .withBatchSize(1)
                            .build(),
                    new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                            .withDriverName("ru.yandex.clickhouse.ClickHouseDriver")
                            .withUrl("jdbc:clickhouse://192.168.21.113:8123/clickhouse")
                            .build());
        }
    }

    @Data
    @Builder
    @ToString
    public static class User {
        @Builder.Default
        @TransientSink
        private String id = "1001";
        private String name;
        @Builder.Default
        private int age = 22;
        @Builder.Default
        private String area = "中国";

    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TransientSink {
    }
}
