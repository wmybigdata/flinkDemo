package com.wmy.java.util.builder.simple;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.builder
 * @Author: wmy
 * @Date: 2021/9/19
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 继承AbstractHouse
 * @Version: wmy-version-01
 */
public class CommonHouse extends AbstractHouse{
    @Override
    public void buildBasic() {
        System.out.println("打地基");
    }

    @Override
    public void buildWalls() {
        System.out.println("垒墙");
    }

    @Override
    public void roofed() {
        System.out.println("盖屋顶");
    }
}
