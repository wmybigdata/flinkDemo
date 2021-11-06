package com.wmy.java.reflect.chapter02;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter02
 * @Author: wmy
 * @Date: 2021/11/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 了解类的加载器
 * @Version: wmy-version-01
 */
public class ClassLoaderTest {
    @Test
    public void test1() {
        ClassLoader classLoader = ClassLoaderTest.class.getClassLoader(); // sun.misc.Launcher$AppClassLoader@18b4aac2 系统类加载器
        System.out.println("系统类加载器：" + classLoader);

        ClassLoader parent = classLoader.getParent(); // 扩展类加载器
        System.out.println("扩展类加载器：" + parent);

        ClassLoader loader = classLoader.getParent().getParent(); // 引导类加载器，我们不可能去能够加载到它，自然也不会让它加载我们写的类
        System.out.println("引导类加载器：" + loader);

        // 对于自定义的类，使用的是系统类加载器
        // 系统类加载器可以使用.getParent()得到扩展类加载器
        // 扩展类加载器是可以使用.getParent()获得引导类加载器
        // 引导类加载器是加载java的核心内库，不能加载用户自定义的加载类

        ClassLoader classLoader1 = String.class.getClassLoader(); // 引导加载器是加载核心内库的
        System.out.println("引导类加载器：" + loader); // 说明String是引导类加载器帮助加载的
    }


    @Test
    public void test2() throws Exception {
        Properties properties = new Properties();
        // 不想使用这种方式，这个的话在idea中相对路径不太好用,这个模式是在module下面的
        //FileInputStream inputStream = new FileInputStream("src/main/resources/jdbc.properties");
        InputStream inputStream = ClassLoaderTest.class.getClassLoader().getResourceAsStream("jdbc.properties"); // 这个配置文件是在当前的module的src目录下面的
        properties.load(inputStream);
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");
        System.out.println("user = " + user + ", password = " + password);
    }
}
