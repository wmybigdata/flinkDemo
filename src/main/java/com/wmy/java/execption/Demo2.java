package com.wmy.java.execption;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Demo2 {

    public static void main(String[] args) {
        Person.PersonBuilder personBuilder = Person.builder().id("1001").name("aaa");

        personBuilder = null;
        System.gc();

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Person {
        private String id;
        private String name;

        protected void finalize() throws Throwable{
            super.finalize();
            System.out.println("我快死了！！！");
        }
    }
}
