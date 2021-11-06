package com.wmy.java.execption;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 自定义异常类
 * @Version: wmy-version-01
 */
public class EcDef extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public EcDef() {

    }

    public EcDef(String msg) {
        super(msg);
    }

}

