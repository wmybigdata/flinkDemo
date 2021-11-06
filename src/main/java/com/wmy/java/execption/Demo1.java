package com.wmy.java.execption;

import com.sun.org.apache.bcel.internal.generic.I2F;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: java异常处理
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static void main(String[] args) {
        Student student = Student.builder().id(-10).build();
        student.test();
    }

    @ToString
    @Builder
    public static class Student {
        int id;

        public void test() {
            if (id > 0) {
                System.out.println("输入的数字大于0。。。");
            } else {
                //throw new RuntimeException("你输入的数据小于0，请输入大于0的数字");
                //throw new Exception("你输入的数据小于0，请输入大于0的数字");
                throw new MyExecption("你输入的数据小于0，请输入大于0的数字");
            }
        }
    }
}
