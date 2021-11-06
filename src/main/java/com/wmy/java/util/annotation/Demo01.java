package com.wmy.java.util.annotation;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.annotation
 * @Author: wmy
 * @Date: 2021/9/20
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: java注解：注解都得结合反射技术
 * @Version: wmy-version-01
 */

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Java提供了一些预定义的注解，其中有四个是元注解，在注解上面的注解
 * 其中两个是需要重点关注
 * @Target 描述Override的适用范围 （目标注解）
 * @Retention 描述Overide的生效范围
 *
 * java.lang.annotation;
 * ElementType：这里面可以了解注解的适用范围：可以限制的作用域
 * RetentionPolicy：生效的范围
 */
@Demo01.SomeAnno(value = "TYPE",name = "TYPE1") // 当某个注解定义了属性，使用时需要赋值
@Demo01.OtherAnno
public class Demo01 {
    @SomeAnno(value = "FIELD",name = "TYPE1") // 如果属性名是value，直接写属性值，但是如果有多个属性名的时候value这个默认属性名是不可以省略，对于有默认值的属性是可以不用进行赋值的
    private int x = 10;

    public static void main(String[] args) throws NoSuchFieldException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<Demo01> aClass = Demo01.class;
        Annotation[] annotations = aClass.getAnnotations(); // 获取类上所有注解
        //System.out.println(Arrays.toString(annotations)); // [@com.wmy.java.util.annotation.Demo01$SomeAnno(), @com.wmy.java.util.annotation.Demo01$OtherAnno()]

        SomeAnno annotation = aClass.getAnnotation(SomeAnno.class);
        //System.out.println(annotation); // @com.wmy.java.util.annotation.Demo01$SomeAnno()

        // 作用是什么勒：ElementType[] value(); 注解的属性，存储的是数组类型的
        String value = annotation.value();
        //System.out.println(value); // TYPE

        String name = annotation.name();
        //System.out.println(name); // TYPE1

        // 用了反射采用框架，有了注解才能使用框架变得非常的轻松
        // 反射获取类的成员变量，并获取成员的变量
        Field f = aClass.getDeclaredField("x");
        SomeAnno annotation1 = f.getAnnotation(SomeAnno.class);
        //System.out.println(annotation1); // @com.wmy.java.util.annotation.Demo01$SomeAnno(name=TYPE1, value=FIELD)

    }

    @Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD}) // 注解能够使用到哪里：这里面几乎可以用到任何地方
    @Retention(RetentionPolicy.RUNTIME) // SOURCE,CLASS,RUNTIME，通过反射技术获取注解
    public @interface SomeAnno {
        // 定义注解类型的属性: 存储类型 属性名()
        //当注解中只有一个属性时，通常名称都是定义value
        // 定义的好处，可以不用写 @SomeAnno(value = "wmy")，直接写 @SomeAnno("wmy")
        String value(); // 使用时需要进行赋值，对于没有初始化的属性，必须进行赋值

        String name() default "TYPE1"; // 注解中可以定义多个属性，注解使用可以使用默认值，对于有默认值的属性是可以不用进行赋值的

    }
    @Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD}) // 注解能够使用到哪里：这里面几乎可以用到任何地方
    @Retention(RetentionPolicy.RUNTIME) // SOURCE,CLASS,RUNTIME，通过反射技术获取注解
    public @interface OtherAnno {

    }
}
