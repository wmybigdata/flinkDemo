package com.wmy.java.util.builder.complex;

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
public abstract class HouseBuilder {
    protected House house = new House();

    // 将建造者的流程写好，抽象的方法
    public abstract void buildBasic();
    public abstract void buildWalls();
    public abstract void roofed();

    // 建造房子，将这个产品给返回去
    public House buildHouse() {
        return house;
    }
}
