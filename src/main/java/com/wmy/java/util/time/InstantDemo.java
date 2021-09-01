package com.wmy.java.util.time;

import java.time.*;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.time
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Instant表示当前(或指定)时间瞬时点，或者瞬间点
 * @Version: wmy-version-01
 */
public class InstantDemo {
    public static void main(String[] args) {
        System.out.println("当前的时间毫秒数：" + System.currentTimeMillis());
        System.out.println("当前的日期和时间：" + LocalDateTime.now());
        System.out.println("当前时间的秒数：" + Instant.now().getEpochSecond());
        System.out.println("Instant时间瞬点：" + Instant.now());
        System.out.println(new Date());
        System.out.println("时间转瞬点：" + new Date().toInstant());

        System.out.println("========================================");
        System.out.println("1 >>> " + LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        System.out.println("2 >>> " + LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
        System.out.println("3 >>> " + LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
        System.out.println("4 >>> " + LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        System.out.println("5 >>> " + LocalDateTime.now().atOffset(OffsetDateTime.now().getOffset()).toEpochSecond());
        System.out.println("6 >>> " + LocalDateTime.now().atOffset(OffsetDateTime.now().getOffset()).toInstant().getEpochSecond());
        System.out.println("7 >>> " + LocalDateTime.now().atOffset(OffsetDateTime.now().getOffset()).toInstant().toEpochMilli());

        System.out.println("========================================");
        Instant instant = Instant.now();
        System.out.println("当前时间的毫秒数: " + new Date(instant.getEpochSecond()*1000));
        System.out.println("当前时间的毫秒数: " + new Date());

        System.out.println("当前时间的毫秒数: " + new Date().toInstant().toEpochMilli());
        System.out.println(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toInstant(ZoneOffset.of("+8")).toEpochMilli());

    }
}
