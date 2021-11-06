package com.wmy.java.reflect.chapter01;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter01
 * @Author: wmy
 * @Date: 2021/9/1
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Cat {


    private String name = "招财猫";

    public Cat() {
    }

    public Cat(String name) {
        this.name = name;
    }
    public void hello() {
        //System.out.println("hello, " + name);
    }

    public void cry() {
        System.out.println(name + " 喵喵叫 。。。");
    }

}
