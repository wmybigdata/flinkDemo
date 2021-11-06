package com.wmy.java.util.builder.complex;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder.complex
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 指挥者，动态的去指定制作的流程
 * @Version: wmy-version-01
 */
public class HouseDirector {
    HouseBuilder houseBuilder = null;

    // 构造器传入HouseBuilder
    public HouseDirector(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    // 通过setter 传入HouseBuilder
    public void setHouseBuilder(HouseBuilder houseBuilder) {
        this.houseBuilder = houseBuilder;
    }

    // 如何处理建造房子的流程，交给指挥者
    public House constructHouse() {
        houseBuilder.buildBasic();
        houseBuilder.buildWalls();
        houseBuilder.roofed();
        return houseBuilder.buildHouse();
    }
}
