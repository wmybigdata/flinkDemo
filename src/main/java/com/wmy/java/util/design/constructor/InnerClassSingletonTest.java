package com.wmy.java.util.design.constructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design
 * @Author: wmy
 * @Date: 2021/9/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 内部类得方式
 * @Version: wmy-version-01
 */
public class InnerClassSingletonTest {
    public static void main(String[] args) {
        InnerClassSingleton instance = InnerClassSingleton.getInstance();
        InnerClassSingleton instance1 = InnerClassSingleton.getInstance();
        System.out.println(instance == instance1);
    }
}
class InnerClassSingleton {
    private static class InnerClassHolder {
        private static InnerClassSingleton innerClassSingleton = new InnerClassSingleton();
    }

    public void say() {
        System.out.println("sdfjladjfa");
    }

    public static InnerClassSingleton getInstance() {
        return InnerClassHolder.innerClassSingleton;
    }
}