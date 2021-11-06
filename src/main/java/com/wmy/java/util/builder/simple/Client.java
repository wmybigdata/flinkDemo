package com.wmy.java.util.builder.simple;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Client {
    public static void main(String[] args) {
        /**
         * 优点：理解简单
         * 缺点：扩展性和维护性不好，产品和创建的过程是一起的，耦合性非常高
         * 解决方法，将产品和产品建造过程解耦 ---> 产品和产品建造的过程解耦
         */
        CommonHouse commonHouse = new CommonHouse();
        commonHouse.build();



    }
}
