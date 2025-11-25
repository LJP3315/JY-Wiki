package com.jycms.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil - 数据库连接工具类
 * IMPORTANT: 请替换下列占位符为你的 MySQL 配置
 */
public class DBUtil {
    // 替换为你的数据库 URL、用户名、密码
    // 示例: jdbc:mysql://localhost:3306/jycms?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
    public static final String DB_URL = "jdbc:mysql://localhost:3306/jycms?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "LiGaB3315";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL Connector/J 8.x
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
