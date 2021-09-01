package com.wmy.java.util.time;

import java.time.LocalTime;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.time
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 时间的案例
 * @Version: wmy-version-01
 */
public class LocalTimeDemo {
    public static void main(String[] args) {
        //============ LocalTime 的构造  ============
        LocalTime localTime = LocalTime.now();                //获取当前时间

        //LocalTime.of(int hour, int minute) 根据参数设置时间，参数分别为时，分
        //LocalTime.of(int hour, int minute, int second) 根据参数设置时间，参数分别为时，分，秒

        LocalTime localTime2 = LocalTime.of(18, 18);
        LocalTime localTime3 = LocalTime.of(18, 18,18);
        System.out.println(localTime2);
        System.out.println(localTime3);

        //============ LoacalDate 获取当前时间属性  ============
        System.out.println(localTime);
        System.out.println(localTime.getHour());
        System.out.println(localTime.getMinute());
        System.out.println(localTime.getSecond());

        System.out.println(localTime.plusHours(1));        //将当前时间加1时
        System.out.println(localTime.plusMinutes(1));    //将当前时间加1分钟
        System.out.println(localTime.plusSeconds(1));    //将当前时间加1秒

        System.out.println(localTime.minusHours(1));    //将当前时间减1小时
        System.out.println(localTime.minusMinutes(1));    //将当前时间减1分钟
        System.out.println(localTime.minusSeconds(1));    //将当前时间减1秒
    }
}
