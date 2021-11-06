package com.wmy.java.reflect.chapter01.question;

import com.wmy.java.reflect.chapter01.Cat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter01.question
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 反射问题的引入
 * @Version: wmy-version-01
 */
public class ReflectionQuestion {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        // 根据配置文件，wmy.properties 指定信息，创建Cat对象并调用方法hello
        // 我们尝试做一做，明白反射
        // 1、使用Properties类读取配置文件
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/main/java/com/wmy/java/reflect/chapter01/wmy.properties"));
        String classfullpath = properties.get("classfullpath").toString();
        String methodName = properties.get("method").toString();
        //System.out.println(classfullpath); // com.wmy.java.reflect.chapter01.Cat
        //System.out.println(method); // hello

        // 2、创建对象
        // OCP原则，开闭原则，不修改源码的情况下，来扩展功能
        // 使用反射机制来解决
        // 2.1 加载class类，Class这个就是一个类
        Class<?> aClass = Class.forName(classfullpath); // 会抛出ClassNotFoundException

        // 2.2 通过aClass得到你加载的类
        Object instance = aClass.newInstance(); // 实例化异常,非法访问异常
        //System.out.println(instance.getClass()); // class com.wmy.java.reflect.chapter01.Cat

        // 2.3 通过其它方法来获取
        // 在反射中，可以把方法视为对象，万物皆对象
        Method method1 = aClass.getMethod(methodName);// NoSuchMethodException，找不到这个类异常
        //System.out.println(method1); // public void com.wmy.java.reflect.chapter01.Cat.hello()

        // 2.4 通过method1调用方法，通过方法的对象来实现调用方法
        // 反射机制是框架的灵魂，是底层基质的灵魂
        // InvocationTargetException：调用目标异常，反射机制，invoke对象
        method1.invoke(instance); // hello, 招财猫 || 招财猫 喵喵叫 。。。

        // 通过外部文件配置，是开闭原则的原理和基础
        // 通过创建类，来进行测试一下

        // 动态代理

        // 这些类都是在java.lang
    }
}
