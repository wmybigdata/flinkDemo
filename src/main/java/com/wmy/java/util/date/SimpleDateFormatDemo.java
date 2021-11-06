package com.wmy.java.util.date;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.date
 * @Author: wmy
 * @Date: 2021/10/27
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: SimpleDateFormat是一个具体的类，用于以区域设置敏感的方式格式化和解析日期。
 * @Version: wmy-version-01
 */
public class SimpleDateFormatDemo {
    public static void main(String[] args) throws Exception{

        // TODO 将时间转换为字符串格式的：格式的话就是日期和时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateToStr = simpleDateFormat.format(new Date());
        System.out.println(dateToStr); // 2021-10-27 15:42:11

        // TODO 将一个字符串的日期格式的数据转换为时间格式的
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strToDate = simpleDateFormat1.parse("2021-10-27 15:42:11");
        //System.out.println(strToDate); // Wed Oct 27 15:42:11 CST 2021
    }

    @Test
    public void test() throws Exception{
        // 1、开始和结束时间
        String startTime = "2021-11-11 00:00:00";
        String endTime = "2021-11-11 00:10:00";

        // 2、小贾、小皮
        String xiaoJia = "2021-11-11 00:03:47";
        String xiaoPi = "2021-11-11 00:10:01";

        // 解析他们的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = simpleDateFormat.parse(startTime);
        Date endDate = simpleDateFormat.parse(endTime);
        Date xiaoJiaDate = simpleDateFormat.parse(xiaoJia);
        Date xiaoPiDate = simpleDateFormat.parse(xiaoPi);

        if (xiaoJiaDate.after(startDate) && startDate.before(endDate)) {
            System.out.println("小贾的时间是在开始和结束之间!!!!");
        } else {
            System.out.println("小贾的时间不在开始和结束之间~~~~");
        }

        if (xiaoPiDate.after(startDate) && xiaoPiDate.before(endDate)) {
            System.out.println("小皮的时间是在开始和结束之间!!!!");
        } else {
            System.out.println("小皮的时间不在开始和结束之间~~~~");
        }
    }
}
