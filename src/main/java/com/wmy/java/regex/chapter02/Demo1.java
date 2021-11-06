package com.wmy.java.regex.chapter02;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.regex.chapter02
 * @Author: wmy
 * @Date: 2021/10/5
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Demo1 {
    @Test
    public void test1(){
        String content = "abc$(a.bc.(123(";
        Pattern regexp = Pattern.compile("\\(");
        Pattern regexp1 = Pattern.compile("\\.");
        Matcher matcher = regexp1.matcher(content);
        while (matcher.find()) {
            System.out.println("找到：" + matcher.group(0));
        }
    }

    @Test
    public void test2() {
        String content = "a11c8abc";
//        String regStr = "[a-z]";
        String regStr = "(?i)abc";
        Pattern regexp = Pattern.compile(regStr,Pattern.CASE_INSENSITIVE);
        Matcher matcher = regexp.matcher(content);
        while (matcher.find()) {
            System.out.println("找到：" + matcher.group(0));
        }
    }
}
