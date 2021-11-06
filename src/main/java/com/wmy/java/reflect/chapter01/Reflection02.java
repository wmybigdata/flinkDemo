package com.wmy.java.reflect.chapter01;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter01
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 测试反射调用对速度的性能和优化操作
 * @Version: wmy-version-01
 */
public class Reflection02 {
    public static void main(String[] args) throws Exception {
        // 最好把输出语句给注销了
        m1(); // M1: 4 秒 。。。
        m2(); // M2: 996 秒 。。。
        // 取消访问权限之后的速度：M2: 560 秒 。。。

        // 这个速度是有很明显的区别的
        //Accessiable获取可执行的，开启和禁用，取消访问检查，默认是true，权限检测
        //Field 查看的类图
        //Method
        //Constructor
    }

    // 传统方法来调用hello
    public static void m1() throws Exception{
        long start = System.currentTimeMillis();
        Cat cat = new Cat();
        for (int i = 0; i < 900000000; i++) {
            cat.hello();
        }
        long end = System.currentTimeMillis();
        System.out.println("M1: " + (end - start) + " 秒 。。。");
    }

    // 反射机制来调用hello
    public static void m2() throws Exception {
        long start = System.currentTimeMillis();

        Class<?> aClass = Class.forName("com.wmy.java.reflect.chapter01.Cat");
        Object instance = aClass.newInstance();
        Method method = aClass.getMethod("hello");
        method.setAccessible(true); // 关闭访问检测
        for (int i = 0; i < 900000000; i++) {
            method.invoke(instance);
        }
        long end = System.currentTimeMillis();
        System.out.println("M2: " + (end - start) + " 秒 。。。");
    }
}
