package com.wmy.yaxin.weilv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.yaxin.weilv
 * @Author: wmy
 * @Date: 2021/10/13
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: Gbase连接测试
 * @Version: wmy-version-01
 */
public class GbaseConnectionTest {
    public static void main(String[] args) {
        Connection connection;
        try {
            Class.forName("com.gbase.jdbc.Driver");
            connection = DriverManager
                    .getConnection(
                            "jdbc:gbase://20.26.5.5:5258/ccoc",
                            "U_OM_APP_COC",
                            "FjXT!1224"
                    );
            System.out.println(connection);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from a_ent_cust_info_202106");
            while (resultSet.next()) {
                String grp_name = resultSet.getString("GRP_NAME");
                System.out.println(grp_name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
