package com.wmy.java.util.annotation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileFilter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.annotation
 * @Author: wmy
 * @Date: 2021/9/20
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 注解版的自动化工厂类
 * @Version: wmy-version-01
 */

/**
 * 1、完整类名，可以在运行时候，对类加载目录进行遍历，获取出所有的.class文件
 * 2、对文件路径进行处理：包名.类名
 * 3、使用Class.forName()获取类名
 * 4、检查某个需要创建对象
 * 5、通过注解来创建对象加一个标记，在运行中获取标记并判断是否为null
 * 6、决定是否要创建对象
 * 7、要创建对象的类：key，通过标记携带（注解属性）
 * 8、对于类中需要装配某个对象的，可以注解为某个标记，进行解析来进行装配工作
 */
@AnnotationFactory.Component
public class AnnotationFactory {
    // 类加载目录的File对昂，进行目录的递归遍历
    private static final File CLASS_LOAD_DIR ;

    // 类加载目录的路径的字符串，进行包名.类名
    private static final String CLASS_LOAD_PATH ;

    // 工程容器：存储需要创建实例的类的Class对象
    private static final Map<String, Class> CLASSES ;

    // 工厂容器：存储工厂创建出的容器
    private static final Map<String, Object> BEANS ;

    // 该属性是需要由工厂装配对象，通过注解来告诉工厂
//    @Autowired
//    private Student student;

    // 静态代码块，工厂初始化操作
    static {
        // 如何获取CLASS_LOAD_DIR，利用ClassLoader加载目录获取资源，获取加载目录的目录
        // 空字符串表示获取当前的目录
        // 对类加载器而言，当前目录就是类加载目录
        String path = AnnotationFactory.class.getClassLoader().getResource("").getPath();
        // 这个字符串不能直接拿来进行使用，但是这个不影响来进行创建文件
        CLASS_LOAD_DIR = new File(path);
        System.out.println(CLASS_LOAD_DIR);

        // 根据类加载目录的File对昂获取类加载路径字符串
        CLASS_LOAD_PATH = CLASS_LOAD_DIR.getAbsolutePath();
        CLASSES = new HashMap<>();
        BEANS = new HashMap<>();

        // 扫描类加载目录
        try {
            scan(CLASS_LOAD_DIR);
            System.out.println(CLASSES.values()); // [class com.wmy.java.util.annotation.AnnotationFactory]
            //System.out.println(CLASSES); // {annotationFactory=class com.wmy.java.util.annotation.AnnotationFactory}
            // 意味着该类需要由工厂来进行管理

            // 手写的东西越少越好，装配对象
            // 扫描完成之后，进行对象的属性对象的装配工作
            autowired();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("工厂初始化失败" + e); // 静态代码块中只能抛出这个了
        }
    }

    /**
     * 递归扫描类加载目录，对所有的.class文件进行处理
     * 将文件路径处理成ClassName，通过反射Class.forName()获取类的class对象
     * 在获取类的注解：@Component，如果某个类上能够获取到注解，说明是需要创建实例的
     * @param file
     */
    private static void scan(File file) throws ClassNotFoundException {
        // 如果file是目录，则获取目录内容，遍历递归
        if (file.isDirectory()) {
            // 根据过滤器规则获取目录内容，只获取子目录和.class文件
            File[] files = file.listFiles(); // 提供过滤器，获取子目录，.class文件
            for (File f : files) {
                scan(f);
            }
        }
        // 判断目标对象是否是我们操作的.class
        String path = file.getAbsolutePath();
        if (!path.endsWith(".class")) {
            return;
        }

        // 对文件路径进行处理path
        // 将路径加载目录的字符串部分给去掉
        path = path.replace(CLASS_LOAD_PATH, "");
        path = path.substring(1, path.lastIndexOf(".class"));
        path = path.replaceAll("[/|\\\\]", "."); // 在java中是\\，转义两个\\

        // 利用反射
        Class<?> aClass = Class.forName(path); // 抛异常

        // 获取类上的注解
        Component component = aClass.getAnnotation(Component.class);
        if (component == null) { // 如果为null，没有注解，工厂是不用管理的，直接进行跳过
            return;
        }
        // 连value都不需要我们进行手写了
        String key = component.value();

        // key==null,获取接口，key!=null
        if (key != null && !key.trim().isEmpty()) {
            CLASSES.put(key, aClass);
            return;
        }
        // 将接口的简单名称首字母小写作为key
        // 将目标类和key存入容器进行备用的操作
        Class<?>[] interfaces = aClass.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                char[] charArray = anInterface.getSimpleName().toCharArray();
                charArray[0] = Character.toLowerCase(charArray[0]);
                String k = new String(charArray);
                CLASSES.put(k, aClass);
            }
            return;
        }

        // 如果目标类没有接口应该怎么办，就直接用类的简单名称作为k
        char[] chars = aClass.getSimpleName().toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        String k = new String(chars);
        CLASSES.put(k, aClass);
    }

    /**
     * 进行对象的装配工作：属性自动装配思路
     * 根据容器中的key将各个对象获取出来，对对象的属性进行装配，不是对类进行装配
     */
    private static void autowired() {
        for (String key : CLASSES.keySet()) {
            // 对对象开始装配
            Object obj = getObject(key);
            autowired(obj);
        }
    }

    /**
     * 对对象进行装配工作,向指定对象的属性进行装配
     * 通过反射技术，扫描对象的成员变量，获取成员变量上的
     * @Autowired 如果不为null，开始装配
     */
    // 这个最好是public
    public static void autowired(Object obj){
        Class<?> aClass = obj.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        // 如果这个对象没有成员变量
        if (declaredFields == null || declaredFields.length == 0) {
            return;
        }

        // 如果对象中有成员变量，则对各个成员变量进行装配
        for (Field declaredField : declaredFields) {
            // 由于是private的，将成员变量设置成可访问的
            declaredField.setAccessible(true); // 对工厂性的临时的操作

            Autowired autowired = declaredField.getAnnotation(Autowired.class);

            // 如果成员变量头上没有注解，跳过
            if (autowired == null) {
                continue;
            }

            // 如果注解value有值，则根据注解的属性值为key，进行对象的获取装配
            String key = autowired.value();

            // 如果key != null || !key.isEmpty()
            if (key == null || key.isEmpty()) {

                // 如果注解的value没有值，以类型小字母小写作为key: Student ---> student
                // 这个跟工厂容器中的key是能对应上的
                // 因为在向工厂存入时，使用的key要么时接口名或者时类名
                // 成员变量类型名和首字母小写对应上
                char[] chars = declaredField.getType().getSimpleName().toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                key = new String(chars);
            }
            // 代码进行优化的问题
            try {
                declaredField.set(obj, getObject(key));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("工厂装配属性出错：" , e);
            }
            continue;
        }
    }

    // 公共的获取对象的方法
    public static Object getObject(String key) {
        System.out.println("key: " + key);
        Object obj = BEANS.get(key);
        try {
            if (obj == null) {
                Class aClass = CLASSES.get(key);
                System.out.println("aClass: " + aClass);
                obj = aClass.newInstance();
                System.out.println("obj :: " + obj);
                BEANS.put(key, obj);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // 当发生异常，则说明从工厂的容器中获取对象失败
            // 从而导致程序运行存在安全隐患
            // 应当让程序终止，让程序员检查排除问题
            throw new RuntimeException("从工厂获取对象 " + key + "失败", e);
        }
        return obj;
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Component {
        String value() default "";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Autowired {
        String value() default "";
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@AnnotationFactory.Component
class Student {
    private String id;
    private String name;
    private int age;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@AnnotationFactory.Component
class Person {
    private String person;
    @AnnotationFactory.Autowired
    private Student student;

    public void a() {
        System.out.println("student: " + student);
    }
}
