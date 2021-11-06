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
public class Client {
    public static void main(String[] args) {
        // 盖普通房子
        CommonHouse commonHouse = new CommonHouse();

        // 准备拆功能键房子的指挥者
        HouseDirector houseDirector = new HouseDirector(commonHouse);

        // 完成盖房，返回产品（房子）
        House house = houseDirector.constructHouse();

        // 如何盖高楼
        HighHouse highHouse = new HighHouse();

        // 重置建造者
        houseDirector.setHouseBuilder(highHouse);

        // 完成盖房子
        houseDirector.constructHouse();
    }
}
