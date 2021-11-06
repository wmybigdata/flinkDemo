package com.wmy.java.util.design.prototypePattern;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design.prototypePattern
 * @Author: wmy
 * @Date: 2021/9/16
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class Football extends Game {

    @Override
    void endPlay() {
        System.out.println("Football Game Finished!");
    }

    @Override
    void initialize() {
        System.out.println("Football Game Initialized! Start playing.");
    }

    @Override
    void startPlay() {
        System.out.println("Football Game Started. Enjoy the game!");
    }
}