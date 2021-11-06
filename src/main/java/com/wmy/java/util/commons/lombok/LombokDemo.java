package com.wmy.java.util.commons.lombok;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.commons.lombok
 * @Author: wmy
 * @Date: 2021/9/17
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: lombok 在生产中的应用
 * @Version: wmy-version-01
 */
public class LombokDemo {

    public static void main(String[] args) {
        Student student = Student
                .builder()
                .no(1001)
                .name("吴明洋")
                .build();
        System.out.println(student);
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Student {
        private int no;
        private String name;
        @Builder.Default
        @TransientSink
        private String sex = "男";
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TransientSink {
    }
}
