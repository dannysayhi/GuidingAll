package org.wood.guidingall.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Danny on 2015/4/20.
 */
public class DBConnect {
    private static Connection conn = null;

    private DBConnect() {
        try {
            Class.forName(Config.JDBC_DRIVER);

            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(Config.DB_URL, Config.USER, Config.PASS);

            System.out.println("Creating statment...");
            conn.createStatement();

            System.out.println("Connected to database successful...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        if(conn == null)
            new DBConnect();
        return conn;
    }
}
