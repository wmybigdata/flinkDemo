package com.wmy.java.util.design.prototypePattern;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.util.design.prototypePattern
 * @Author: wmy
 * @Date: 2021/9/16
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 使用 Game 的模板方法 play() 来演示游戏的定义方式。
 * @Version: wmy-version-01
 */
public class TemplatePatternDemo {
    public static void main(String[] args) {

        Game game = new Cricket();
        game.play();
        System.out.println();
        game = new Football();
        game.play();
    }
}
