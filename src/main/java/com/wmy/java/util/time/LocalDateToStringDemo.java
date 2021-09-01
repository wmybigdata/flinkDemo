package com.wmy.java.util.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
 * @Desription:
 * @Version: wmy-version-01
 */
public class LocalDateToStringDemo {
    public static void main(String[] args) {
        //localDate --> String
        LocalDate localDate = LocalDate.now();
        String format1 = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);    //yyyyMMdd
        String format2 = localDate.format(DateTimeFormatter.ISO_DATE);            //yyyy-MM-dd


        //2.LocalTime  --> String
        LocalTime localTime = LocalTime.now();
        String format3 = localTime.format(DateTimeFormatter.ISO_TIME);            //20:19:22.42
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        String format4 = localTime.format(formatter);

        //3.LocalDateTime  --> String
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String format5 = localDateTime.format(formatter2);

        System.out.println(format1);
        System.out.println(format2);
        System.out.println(format3);
        System.out.println(format4);
        System.out.println(format5);

        LocalDateTime localDateTime1 = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        System.out.println("日期时间类型转换成字符串" + localDateTime1.format(formatter2));
    }
}

