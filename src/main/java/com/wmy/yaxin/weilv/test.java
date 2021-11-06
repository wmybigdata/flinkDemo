package com.wmy.yaxin.weilv;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.yaxin.weilv
 * @Author: wmy
 * @Date: 2021/10/13
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class test {
    private static long diff = 0;
    private static int i = 0;
    private static long end = 0L;

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        while (true) {
            end = System.currentTimeMillis();
            diff = (end - start) / 1000L;
            System.out.println(i++);
            if (diff == 60) {
                break;
            }
        }

    }
}
