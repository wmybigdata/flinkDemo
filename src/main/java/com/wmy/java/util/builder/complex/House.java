package com.wmy.java.util.builder.complex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder.complex
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class House {
    private String baise;
    private String wall;
    private String roofed;
}
