package com.wmy.java.execption;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 用户自定义异常
 * @Version: wmy-version-01
 */

/**
 * 如何自定义异常类：
 * 1、继承现有类的异常结构：RuntimeException、Exception
 * 2、提供serialVersionUID全局常量：序列化机制
 * 3、提供重载的构造器
 */
public class MyExecption extends RuntimeException {
    static final long serialVersionUID = -7034897190745766939L; // 序列化机制

    public MyExecption() {

    }

    public MyExecption(String message) {
        super(message);
    }


}
