package com.wmy.java.arithmetic.kmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.arithmetic.kmp
 * @Author: wmy
 * @Date: 2021/9/26
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 判断是否是字串的问题
 * @Version: wmy-version-01
 */
public class Demo1 {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String str1 = bufferedReader.readLine();
        String str2 = bufferedReader.readLine();
        System.out.print(helper(str1, str2)==true ? "YES" : "NO");
    }


    private static boolean helper(String str1, String str2) {
        if (str1 == null || str2 == null || str1.length() != str2.length()) {
            return false;
        }
        return (str1 + str1).contains(str2);
    }
}
