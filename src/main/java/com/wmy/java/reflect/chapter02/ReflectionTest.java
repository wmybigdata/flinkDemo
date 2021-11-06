package com.wmy.java.reflect.chapter02;

import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter02
 * @Author: wmy
 * @Date: 2021/11/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 反射的测试
 * @Version: wmy-version-01
 */
public class ReflectionTest {
    @Test
    public void test1() {
        // 反射之前对Person类的操作
        Person person = new Person("Tom", 12);

        // 通过对象调用属性和方法
        person.setAge(10);
        System.out.println(person);
        person.show();

        // 在Person外部是不可以用对象调用其私有的结构
        // 比如私有的属性和方法以及构造器
    }

    @Test
    public void test2() throws Exception {
        Class<? extends Person> personClass = Person.class; // 通过反射来进行获取对象
        Constructor<? extends Person> constructor = personClass.getConstructor(String.class, Integer.class); // 记住这里传入的类型一定要和属性中的参数的类型是一致的
        Person person = constructor.newInstance("Tom", 12);
        Field age = personClass.getDeclaredField("age"); // 通过反射来获取属性
        age.setAccessible(true); // 跳过安全认证检查，这个是可以调用私有的属性和方法
        age.set(person, 109); // 通过属性给对象进行复制
        Method show = personClass.getDeclaredMethod("show"); // 通过这个类来进行调用方法
        show.invoke(person); // 通过方法来进行调佣对象

        // TODO : 注意、如果说是给属性赋值的直接使用属性.set(对象,值)就可以进行赋值了
        // TODO : 如果说是方法执行的，方法调用invoke(对象)就可以实现了

        // 反射的机制和面向对象之间的 关系是矛盾的
        // 直接new的方式或者反射的方式都是可以调用公共的接口
        // 开发中到底是在用哪个，建议的话是直接使用new的方式，从代码量上面体现大多数的情况
        // 什么时候会去使用反射的方式：反射的特征、动态性
    }

    @Test
    public void test3() throws Exception {
        // 方式一
        Class<Person> personClass = Person.class;

        // 方式二
        Person person = new Person();
        Class<? extends Person> personClass1 = person.getClass();

        // 方式三
        Class<?> aClass = Class.forName("com.wmy.java.reflect.chapter02.Person");
        System.out.println(aClass);

        // 方式四
        ClassLoader classLoader = ReflectionTest.class.getClassLoader();
        Class<?> aClass1 = classLoader.loadClass("com.wmy.java.reflect.chapter02.Person");
        System.out.println(aClass1);
    }

    // 类的反射的类型、只要是数组元素的类型和维度是一样就是同一个Class对象
    @Test
    public void test4() {
        Class<Object> objectClass = Object.class;
        Class<Comparable> comparableClass = Comparable.class;
        Class<String[]> aClass = String[].class;
        Class<int[][]> aClass1 = int[][].class;
        Class<ElementType> elementTypeClass = ElementType.class;
        Class<Override> overrideClass = Override.class;
        Class<Integer> integerClass = int.class;
        Class<Void> voidClass = void.class;
        Class<Class> classClass = Class.class;
        int[] ints = new int[10];
        Class<? extends int[]> aClass2 = ints.getClass();
        int[] ints1 = new int[100];
        Class<? extends int[]> aClass3 = ints1.getClass();
    }
}
