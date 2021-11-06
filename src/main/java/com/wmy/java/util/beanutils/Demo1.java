package com.wmy.java.util.beanutils;

import com.alibaba.fastjson.JSON;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.webapp.hamlet2.Hamlet;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.beanutils
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: java 操作beanutils的类
 * @Version: wmy-version-01
 */
public class Demo1 {
    @Test
    public void test1() throws InvocationTargetException, IllegalAccessException {
        Person person = new Person();
        BeanUtils.setProperty(person,"name","a");
        BeanUtils.setProperty(person,"age","21");
        System.out.println(person);
    }

    @Test
    public void test2() throws InvocationTargetException, IllegalAccessException {
        // 转换数据类型只是针对于基本数据类型
        // 注册转换器来进行转换
        Person person = new Person();
        String name = "a";
        String password = "b";
        String age = "21";
        BeanUtils.setProperty(person, "name", name);
        BeanUtils.setProperty(person, "password", password);
        BeanUtils.setProperty(person, "age", age);
        System.out.println(person);
    }

    @Test
    public void test3() throws InvocationTargetException, IllegalAccessException {
        // 但是原生自带的转换器是有问题的，会有bug，所以是自己去自定义去实现这个转换器
        // 转换数据类型只是针对于基本数据类型
        // 注册转换器来进行转换
        Person1 person = new Person1();
        String name = "a";
        String password = "b";
        String age = "21";
        String birthday = "2021-09-19";

        // 为了让日期赋值导bean的birthday属性上，我们给beanUtils注册一个日期转换器
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class aClass, Object o) {
                if (o == null) {
                    return null;
                }
                if (!(o instanceof String)) {
                    throw new ConversionException("Beanutils 支持String类型的时间转换");
                }
                String birthday = (String) o;
                if (StringUtils.isBlank(birthday)) {
                    return null;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                // 这个方法不能抛出出去
                try {
                    return simpleDateFormat.parse(birthday);
                } catch (ParseException e) {
                    // 这个不能直接进行打印的，出现异常一定要进行中断
                    throw new RuntimeException(e);
                }
            }
        },Date.class);

        BeanUtils.setProperty(person, "name", name);
        BeanUtils.setProperty(person, "password", password);
        BeanUtils.setProperty(person, "age", age);
        BeanUtils.setProperty(person, "birthday", birthday);


        System.out.println(person);
    }


    @Test
    public void test4() throws InvocationTargetException, IllegalAccessException {
        // 但是原生自带的转换器是有问题的，会有bug，所以是自己去自定义去实现这个转换器
        // 转换数据类型只是针对于基本数据类型
        // 注册转换器来进行转换
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("name", "吴明洋");
        map.put("password", "123");
        map.put("age", "21");
        map.put("birthday", "");//2021-09-19
        if (StringUtils.isBlank(map.get("birthday"))) {
            map.put("birthday", "2021-09-19");
        }
        Person2 person2 = new Person2();
        BeanUtils.populate(person2, map);
        System.out.println(person2);
    }

    @Test
    public  void test5() {
        // https://www.cnblogs.com/mafy/p/12810127.html
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("================= sdf ======================");
        //System.out.println("date: " + date);
        System.out.println(simpleDateFormat.format(date));
        //System.out.println(date.toInstant().toString());

        System.out.println("============ Date 转 LocalDateTime 时间类型的 ============");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        LocalDateTime localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String format = localDate.format(formatter2);
        System.out.println(format);

        System.out.println("============ LocalDateTime 转 DateTime 时间类型的 ============");
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String format5 = localDateTime.format(formatter1);
        System.out.println(format5);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Person {
        private String name;
        @Builder.Default
        private String password = "a";
        private int age;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person1 {
        private String name;
        @Builder.Default
        private String password = "a";
        private int age;
        private Date birthday;

        @Override
        public String toString() {
            return "Person1{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    ", age=" + age +
                    ", birthday=" + birthday + // .toInstant().toString().substring(0,10)
                    '}';
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person2 {
        private String name;
        @Builder.Default
        private String password = "a";
        private int age;
        private String birthday;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TransientSink {
    }
}
