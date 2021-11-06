package com.wmy.java.execption;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.java.execption
 * @Author: wmy
 * @Date: 2021/11/4
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription:
 * @Version: wmy-version-01
 */
public class EcmDef {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            int i = Integer.parseInt(args[0]);
            int j = Integer.parseInt(args[1]);
            int result = ecm(i, j);
            System.out.println(result);
        } catch (NumberFormatException e) {
            System.out.println("数据类型不一致");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("缺少命令行参数");
        } catch (ArithmeticException e) {
            System.out.println("除数不能为0");
        } catch (EcDef ecDef) {
            System.out.println(ecDef.getMessage());
        }
    }

    public static int ecm(int i, int j) throws EcDef {
        if (i < 0 || j < 0) {
            throw new EcDef("分子和分母不能小于0");
        }
        return i / j;
    }
}
