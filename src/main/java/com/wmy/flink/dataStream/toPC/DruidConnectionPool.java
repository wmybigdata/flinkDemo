package com.wmy.flink.dataStream.toPC;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import scala.sys.Prop;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @project_name: flinkDemo
 * @package_name: com.wmy.flink.dataStream.toPC
 * @Author: wmy
 * @Date: 2021/10/27
 * @Major: 数据科学与大数据技术
 * @Post：大数据实时开发
 * @Email：wmy_2000@163.com
 * @Desription: druid连接池
 * @Version: wmy-version-01
 */
public class DruidConnectionPool {
    private transient static DataSource dataSource = null;
    private transient static Properties properties = new Properties();

    static {
        try {
            properties.put("driverClassName", "com.mysql.jdbc.Driver");
            properties.put("url", "jdbc:mysql://172.16.200.101:3306/bigdata?characterEncoding=UTF-8");
            properties.put("username", "root");
            properties.put("password", "123456");
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException("创建Druid连接池失败。。。");
        }
    }

    public DruidConnectionPool() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
