package com.wmy.java.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.annotation
 * @Author: wmy
 * @Date: 2021/9/20
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Demo02 {
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        // 完整类名，可以在运行时，对类加载目录进行遍历，获取出所有的.class文件
        // 对文件路径名进行处理，得到包名
        // 可以通过Class.forName()来进行获取
        Class<?> aClass = Class.forName("com.wmy.java.util.annotation.SomeServiceImpl");
        Component1 annotation = aClass.getAnnotation(Component1.class);
        // 判断是否需要创建对象
        // 让工厂模式脱离配置文件的好处是什么。避免了配置文件出错
        if (annotation != null) {
            String key = annotation.value();
            Object newInstance = aClass.newInstance();
            System.out.println(newInstance); // com.wmy.java.util.annotation.SomeServiceImpl@35bbe5e8
            System.out.println(key); // someservice
        }
    }
}

// 工厂创建对象需要一个配置文件
// 不写配置文件，在程序运行过程中，检查某个类是否需要创建对象
// 通过注解来加标记判断是为null
interface SomeService {

}


// 给需要创建类的对象加入注解
@Component1("someservice")
class SomeServiceImpl implements SomeService {

}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) // 运行中通过反射来进行获取出来
@interface Component1 {
    String value();
}