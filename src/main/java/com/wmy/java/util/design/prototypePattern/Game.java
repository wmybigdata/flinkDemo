package com.wmy.java.util.design.prototypePattern;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design.prototypePattern
 * @Author: wmy
 * @Date: 2021/9/16
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 创建一个抽象类，它的模板方法被设置为 final。
 * @Version: wmy-version-01
 */
public abstract class Game {
    abstract void initialize();
    abstract void startPlay();
    abstract void endPlay();

    //模板
    public final void play(){

        //初始化游戏
        initialize();

        //开始游戏
        startPlay();

        //结束游戏
        endPlay();
    }
}
