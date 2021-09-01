package com.wmy.java.util.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: LocalDate表示当前(或指定)日期，格式为：yyyy-MM-dd
 * @Version: wmy-version-01
 */
public class LocalDateDemo {
    public static void main(String[] args) {

        LocalDate localDate = LocalDate.now(); // 获取当前时间：yyyy-MM-dd ---> 2021-09-01
        System.out.println(localDate);

        LocalDate localDate2 = LocalDate.of(2099, 12, 12); // 可以指定具体的年月日时间
        System.out.println(localDate2); // 2099-12-12

        //============ LoacalDate 获取当前时间属性  ============

        System.out.println(localDate.getYear());               //获取当前年份:2019
        System.out.println(localDate.getMonth());              //获取当前月份，英文：DECEMBER
        System.out.println(localDate.getMonthValue());         //获取当前月份，数字：12

        System.out.println(localDate.getDayOfMonth());         //获取当前日期是所在月的第几天7
        System.out.println(localDate.getDayOfWeek());          //获取当前日期是星期几（星期的英文全称）:SATURDAY
        System.out.println(localDate.getDayOfYear());          //获取当前日期是所在年的第几天:341

        System.out.println(localDate.lengthOfYear());          //获取当前日期所在年有多少天
        System.out.println(localDate.lengthOfMonth());         //获取当前日期所在月份有多少天
        System.out.println(localDate.isLeapYear());            //获取当前年份是否是闰年

        //============ LoacalDate 当前时间的加减  ============

        System.out.println(localDate.minusYears(1));           //将当前日期减1年
        System.out.println(localDate.minusMonths(1));          //将当前日期减1月
        System.out.println(localDate.minusDays(1));            //将当前日期减1天

        System.out.println(localDate.plusYears(1));            //将当前日期加1年
        System.out.println(localDate.plusMonths(1));           //将当前日期加1月
        System.out.println(localDate.plusDays(1));             //将当前日期加1天

        //============ LoacalDate 当前时间的判断 ============
        System.out.println("LoacalDate的判断");
        System.out.println(localDate.isAfter(localDate2));                    //localDate在localDate2日期之后
        System.out.println(localDate.isBefore(localDate2));                    //localDate在localDate2日期之前
        System.out.println(localDate.isEqual(localDate2));                    //localDate和localDate2日期是否相等

        //============ LoacalDate 当前时间支持的类型  ============
        System.out.println(localDate.isSupported(ChronoField.DAY_OF_YEAR));    //当前时间支持的时间类型是：一年中的某一天，这个不知道应用场景
        System.out.println(localDate.isSupported(ChronoUnit.DAYS));            //当前日期支持的单元：天(说明当前时间是按天来算的)

        System.out.println(localDate.with(TemporalAdjusters.firstDayOfMonth()));            //本月的第1天
        System.out.println(localDate.with(TemporalAdjusters.firstDayOfNextMonth()));        //下月的第1天
        System.out.println(localDate.with(TemporalAdjusters.firstDayOfNextYear()));         //下年的第1天

        //============ LocalDate 指定时间的操作  ===============　　　　
        System.out.println(localDate.withDayOfMonth(2));                                    //本月的第几天
        System.out.println(localDate.with(TemporalAdjusters.lastDayOfMonth()));             //本月的最后一天
        System.out.println(localDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)));   //上一周星期天是几号
        System.out.println(localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY)));       //下一周星期一是几号

    }
}
