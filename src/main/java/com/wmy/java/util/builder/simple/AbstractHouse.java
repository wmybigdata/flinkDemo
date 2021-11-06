package com.wmy.java.util.builder.simple;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 建造者者模式
 * @Version: wmy-version-01
 */
public abstract class AbstractHouse {
    // 打地基
    public abstract void buildBasic();

    // 垒墙
    public abstract void buildWalls();

    // 封顶
    public abstract void roofed();

    public void build() {
        buildBasic();
        buildWalls();
        roofed();
    }
}
