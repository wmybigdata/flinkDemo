package com.wmy.java.reflect.chapter02;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.reflect.chapter02
 * @Author: wmy
 * @Date: 2021/11/6
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {
    private String name;
    private Integer age;

    public void show() {
        System.out.println("你好，我是一个人。。。");
    }

    public String showNation(String nation) {
        System.out.println("我的国籍是：" + nation);
        return nation;
    }
}
