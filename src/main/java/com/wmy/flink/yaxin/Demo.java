package com.wmy.flink.yaxin;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.yaxin
 * @Author: wmy
 * @Date: 2021/9/26
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Demo {
    public static void main(String[] args) {
        String str = ",,18267598878,,,18267598878,20210525151243,20210525151355,22609,38052,22609,38052,0,1,12,255,534502,1,30700";
        String[] split = str.split("[,]", -1);
        System.out.println(split.length);
    }
}
