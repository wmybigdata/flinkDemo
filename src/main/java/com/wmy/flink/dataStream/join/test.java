package com.wmy.flink.dataStream.join;

import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.join
 * @Author: wmy
 * @Date: 2021/9/7
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class test {
    public static void main(String[] args) {
        Map<Integer, String> hashMap = new HashMap();
        hashMap.put(1001, "beijing");
        hashMap.put(1002, "shanghai");
        hashMap.put(1003, "wuhan");
        hashMap.put(1004, "changsha");
        System.out.println(hashMap.get(1004));
    }
}
