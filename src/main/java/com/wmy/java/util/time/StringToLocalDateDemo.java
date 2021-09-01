package com.wmy.java.util.time;

import java.text.DateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.time
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 字符串转时间类型的工具类
 * @Version: wmy-version-01
 */
public class StringToLocalDateDemo {
    public static void main(String[] args) {
        // String --> LocalDate
        LocalDate localDate1 = LocalDate.parse("2019-12-07");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        System.out.println(LocalDate.parse("2019-10-09").format(pattern));

        // String --> LocalTime
        LocalTime localTime = LocalTime.parse("07:43:53");

        // String -->LocalDateTime
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"); // 12小时
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 24小时
        LocalDate localDate2 = LocalDate.parse("2019-12-07 07:43:53",formatter2);

        System.out.println(localDate1);
        System.out.println(localTime);
        System.out.println(localDate2);


        LocalDateTime localDateTime = LocalDateTime.parse("2021-09-01 10:10:10", formatter2); // 2021-09-01T10:10:10
        System.out.println("字符串转时间：" + localDateTime);
        System.out.println(" >>> " + formatter2.format(localDateTime));
        System.out.println(localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
    }
}
