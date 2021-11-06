package com.wmy.java.reflect.chapter01;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter01
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: java反射
 * @Version: wmy-version-01
 */
public class Reflection01 {
    public static void main(String[] args) throws Exception {

        // 1、读取配置文件
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/java/com/wmy/java/reflect/chapter01/wmy.properties"));
        String classfullpath = properties.get("classfullpath").toString();
        String methodName = properties.get("method").toString();


        // 2、反射
        // 通过方法调对象
        Class<?> aClass = Class.forName(classfullpath);
        Object instance = aClass.newInstance();
        Method method = aClass.getMethod(methodName);
        method.invoke(instance);

        // 如何拿取字段
        // Fields
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            System.out.println(declaredField.getName());
            System.out.println(declaredField.get(instance));
        }

        // 如何去调用一个构造方法
        Constructor<?> constructor = aClass.getConstructor();
        System.out.println(constructor);

        // 加入如果带参数的构造器怎么办
        Constructor<?> constructor1 = aClass.getConstructor(String.class); // 这个是String这个类的构造对象
        System.out.println(constructor1);


        // 构造器，创建对象，又可以去构建对象

        /*
        反射的优点：
            反射是框架的灵魂，使用灵魂，底层支持
            动态语言，核心要素
        缺点：
            解释执行，对执行速度有影响
         */
    }
}
