package com.wmy.yaxin.weilv;


import java.sql.*;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.yaxin.weilv
 * @Author: wmy
 * @Date: 2021/10/13
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: 测试jdbc批量load操作
 * @Version: wmy-version-01
 */
public class GbaseConnectionBatchLoad {
    private static long diff = 0;
    private static int i = 0;
    private static long end = 0L;
    private static int count = 0;

    public static void main(String[] args) {

        try {
            Class.forName("com.gbase.jdbc.Driver");
            Connection conn = null;
            PreparedStatement preparedStatement = null;
            Class.forName("com.gbase.jdbc.Driver");
            conn = DriverManager
                    .getConnection(
                            "jdbc:gbase://20.26.5.5:5258/ccoc",
                            "U_OM_APP_COC",
                            "FjXT!1224"
                    );
            conn.setAutoCommit(false);
            String sql = "insert into `user_info` (`user_id`,`user_name`, `user_info`)values(?, ?,?)";
            preparedStatement = conn.prepareStatement(sql);

            long start = System.currentTimeMillis();
            for (int i = 0; i < 50000; i++) {

                end = System.currentTimeMillis();
                diff = (end - start) / 1000L;

                preparedStatement.setInt(1, i + 1);
                preparedStatement.setString(2, i + 1 + "");
                preparedStatement.setString(3, i + 1 + "");
                count++;
                preparedStatement.addBatch();

//                if (diff == 1) {
//                    System.out.println(count);
//                    break;
//                }

                if (i % 5000 == 0) {
                    preparedStatement.executeBatch();
                    System.out.println("执行了 5000 条数据 。。。");

                    if (diff == 60) {
                        count = count + 5000;
                        System.out.println(count);
                        break;
                    }
                    conn.commit(); // 12096288
                }
            }
            // 剩下的执行
            preparedStatement.executeBatch();
            conn.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
