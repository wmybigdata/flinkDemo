package com.wmy.java.util.builder.stringBuilder;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder.stringBuilder
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: StringBuilder是一个建造者模式
 * @Version: wmy-version-01
 */
public class Builder {
    public static void main(String[] args) {
        StringBuilder stringBuilder = new StringBuilder("hello,world");
        System.out.println(stringBuilder);

        // 指挥者是谁，查看源码的时候，有可能发现使用的时候，设计模式的思想一样，形式上有可能是不一致的
        /**
         * StringBuilder 指挥者角色和建造者模式
         * 建造方法的实现是由AbstractStringBuilder来进行实现的，而我们的StringBuilder继承了AbstractStringBuilder
         * AbstractStringBuilder 实现了Appendable接口中的方法，已经是一个建造者了，只是不能实例化操作
         * Appendable 抽象的构造者
         */

    }
}
