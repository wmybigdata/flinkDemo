package com.wmy.java.util.date;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.date
 * @Author: wmy
 * @Date: 2021/10/27
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: java对Date的认识
 * @Version: wmy-version-01
 */
public class DateDemo {
    public static void main(String[] args) {

        //System.out.println(System.currentTimeMillis()); // 1635318939312
        Date beforTimeStampDate = new Date(1635318939312L); // 接受一个毫秒的时间戳类型

        Date date = new Date();
        //System.out.println(date); // Wed Oct 27 15:08:23 CST 2021

        Date date1 = new Date(System.currentTimeMillis()); // 接受一个毫秒的时间戳类型
        // System.out.println(date1); // Wed Oct 27 15:08:57 CST 2021

        boolean after = beforTimeStampDate.after(new Date()); // 测试此日期是否在指定日期之后。
        //System.out.println(after); // false

        boolean before = beforTimeStampDate.before(new Date()); // 测试此日期是否在指定日期之前
        //System.out.println(before); // true

        int compareTo = beforTimeStampDate.compareTo(new Date());
        //System.out.println(compareTo); // -1：小于，0：相等，1：大于

        boolean equals = beforTimeStampDate.equals(new Date()); // 判断这两个时间是否相等
        //System.out.println(equals); // false

        //System.out.println(Instant.now()); // Instant：微秒、Date：是毫秒级别
        Date from = Date.from(Instant.now());
        //System.out.println(from); // Wed Oct 27 15:23:31 CST 2021

        String string = beforTimeStampDate.toString();
        //System.out.println(string); // Wed Oct 27 15:15:39 CST 2021

    }
}
