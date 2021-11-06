package com.wmy.java.execption;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: UseConcurrentMap
 * @Version: wmy-version-01
 */
public class UseConcurrentMap {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Object> chm = new ConcurrentHashMap<String, Object>();
        chm.put("k1", "v1");
        chm.put("k2", "v2");
        chm.put("k3", "v3");
        chm.putIfAbsent("k4", "vvvv");
        chm.putIfAbsent("5", "555");

        for (Map.Entry<String, Object> stringObjectEntry : chm.entrySet()) {
            System.out.println(stringObjectEntry.getKey() + "\t" + stringObjectEntry.getValue());
        }

        Iterator iter = chm.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry me = (Map.Entry) iter.next();
            System.out.println("key:" + me.getKey() + ",value:" + me.getValue());
        }
    }
}
