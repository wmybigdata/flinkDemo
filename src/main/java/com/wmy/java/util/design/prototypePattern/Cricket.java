package com.wmy.java.util.design.prototypePattern;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design.prototypePattern
 * @Author: wmy
 * @Date: 2021/9/16
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 创建扩展了上述类的实体类。
 * @Version: wmy-version-01
 */
public class Cricket extends Game {

    @Override
    void endPlay() {
        System.out.println("Cricket Game Finished!");
    }

    @Override
    void initialize() {
        System.out.println("Cricket Game Initialized! Start playing.");
    }

    @Override
    void startPlay() {
        System.out.println("Cricket Game Started. Enjoy the game!");
    }
}