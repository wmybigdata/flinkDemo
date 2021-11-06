package com.wmy.java.util.commons.lang3;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.commons.lang3
 * @Author: wmy
 * @Date: 2021/9/17
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 使用工具类对字符串来进行操作
 * @Version: wmy-version-01
 */
public class WmyStringUtils {
    /**
     * isBlank: 判断更严谨，包含的有空串("")、空白符(空格""，"  "，制表符"\t"，回车符"\r"，"\n"等)以及null值
     * isEmpty：包含是空串("")和null值，不包含空白符；
     */
    @Test
    public void strIsNull() {
        String str1 = "";
        String str2 = null;
        String str3 = " ";
        String str4 = "    ";
        System.out.println("========================");
        System.out.println(str3.length());
        System.out.println(str4.length());
        System.out.println("========================");
        System.out.println(StringUtils.isBlank(str1));
        System.out.println(StringUtils.isBlank(str2));
        System.out.println(StringUtils.isBlank(str3));
        System.out.println(StringUtils.isBlank(str4));

        System.out.println("=========================");
        System.out.println(StringUtils.isEmpty(str1));
        System.out.println(StringUtils.isEmpty(str2));
        System.out.println(StringUtils.isEmpty(str3));
        System.out.println(StringUtils.isEmpty(str4));
    }


}
