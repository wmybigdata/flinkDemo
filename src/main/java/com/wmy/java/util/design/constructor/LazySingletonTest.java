package com.wmy.java.util.design.constructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design
 * @Author: wmy
 * @Date: 2021/9/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 懒加载设计模式
 * @Version: wmy-version-01
 */
public class LazySingletonTest {
    public static void main(String[] args) {
        LazySingleton instance = LazySingleton.getInstance();
        LazySingleton instance1 = LazySingleton.getInstance();
        System.out.println(instance == instance1);
    }
}

class LazySingleton {
    private static LazySingleton lazySingleton;
    public static LazySingleton getInstance() {
        if (lazySingleton == null) {
            synchronized (LazySingleton.class) {
                if (lazySingleton == null) {
                    lazySingleton = new LazySingleton();
                }
            }
        }
        return lazySingleton;
    }
}