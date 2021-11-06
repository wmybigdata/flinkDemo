package com.wmy.java.reflect.chapter01;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter01
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 演示class不是创建出来的，是加载出来的
 * @Version: wmy-version-01
 */
public class Reflection03 {
    public static void main(String[] args) throws ClassNotFoundException {
        // 1、class也是类
        // 2、class不是创建出来的
        Cat cat = new Cat();
        Class<?> aClass1 = Class.forName("com.wmy.java.reflect.chapter01.Cat");
        Class<?> aClass2 = Class.forName("com.wmy.java.reflect.chapter01.Cat");
        System.out.println(aClass1.hashCode());
        System.out.println(aClass2.hashCode());
    }
}
