package com.wmy.java.util.design.constructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design
 * @Author: wmy
 * @Date: 2021/9/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 饥饿模式
 * @Version: wmy-version-01
 */
public class HungrySingletonTest {
    public static void main(String[] args) {
        HungrySingleton instance = HungrySingleton.getInstance();
        HungrySingleton instance1 = HungrySingleton.getInstance();
        System.out.println(instance == instance1);

        new Thread(() -> {
            HungrySingleton instance2 = HungrySingleton.getInstance();
            System.out.println(instance2);
        }).start();

        new Thread(() -> {
            HungrySingleton instance3 = HungrySingleton.getInstance();
            System.out.println(instance3);
        }).start();
    }
}

class  HungrySingleton{
    private static HungrySingleton hungrySingleton = new HungrySingleton(); // 类加载模式

    public static HungrySingleton getInstance() {
        return hungrySingleton;
    }
}