package com.wmy.java.util.date;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.date
 * @Author: wmy
 * @Date: 2021/10/27
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 所述 Calendar类是一个抽象类，可以为在某一特定时刻和一组之间的转换的方法
 *  calendar fields如 YEAR ， MONTH ， DAY_OF_MONTH ， HOUR ，等等，以及用于操纵该日历字段，如获取的日期下个星期。
 *
 *  Calendar.Builder ----> Calendar.Builder用于创建一个 Calendar从各个日期-时间参数。
 * @Version: wmy-version-01
 */
public class CalendarDemo {
    public static void main(String[] args) throws Exception{
        Calendar c = Calendar.getInstance();
        System.out.println(c.getTime());

        // 获得年份
        System.out.println("现在是：" + c.get(Calendar.YEAR) + "年");

        // 获得月份
        System.out.println("现在是：" + (c.get(Calendar.MONTH)+1) + "月");

        //获得日期
        System.out.println("现在是：" + c.get(Calendar.DATE) + "号");
        System.out.println("现在是：" + c.get(Calendar.DAY_OF_MONTH) + "号");

        // 获得这是今年的第几天
        System.out.println("现在是今年第" + c.get(Calendar.DAY_OF_YEAR) + "天");

        // 获得今天周几
        System.out.println("现在是星期：" + (c.get(Calendar.DAY_OF_WEEK)-1) );

        // 获得今天是这个月的第几周
        System.out.println("现在是第：" + c.get(Calendar.DAY_OF_WEEK_IN_MONTH) + "周" );

        // 12小时制的时间
        System.out.println("现在是：" + c.get(Calendar.HOUR) + "点");

        // 24小时制的时间
        System.out.println("现在是：" + c.get(Calendar.HOUR_OF_DAY) + "点");

        // 分钟数
        System.out.println("现在是：" + c.get(Calendar.MINUTE) + "分");

        // 秒数
        System.out.println("现在是：" + c.get(Calendar.SECOND) + "秒");

        // 毫秒
        System.out.println("现在是：" + c.get(Calendar.MILLISECOND) + "毫秒");

        SimpleDateFormat dc = new SimpleDateFormat();
        dc.applyPattern("yyyy-MM-dd");
        Date d = dc.parse("2017-5-13");
        c.setTime(d);

        System.out.println("--------------------2017-5-13信息-----------------------");

        // 获得年份
        System.out.println("现在是：" + c.get(Calendar.YEAR) + "年");

        // 获得月份
        System.out.println("现在是：" + (c.get(Calendar.MONTH)+1) + "月");

        //获得日期
        System.out.println("现在是：" + c.get(Calendar.DATE) + "号");
        System.out.println("现在是：" + c.get(Calendar.DAY_OF_MONTH) + "号");

        // 获得这是今年的第几天
        System.out.println("现在是今年第" + c.get(Calendar.DAY_OF_YEAR) + "天");

        // 获得今天周几
        System.out.println("现在是星期：" + (c.get(Calendar.DAY_OF_WEEK)-1) );

        // 获得今天是这个月的第几周
        System.out.println("现在是第：" + c.get(Calendar.DAY_OF_WEEK_IN_MONTH) + "周" );

    }
}
