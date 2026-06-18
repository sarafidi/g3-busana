//Singleton pattern implementation for DBConnection class

package com.busana.service;

import java.sql.Connection;
import java.sql.DriverManager;



public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private static final String url = "jdbc:mysql://localhost:3306/busana";
    private static final String username = "root";
    private static final String password = "1234";

    private DBConnection() {
        // System.out.println("==============DBConnection instance created.=================");
        // connect();
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("==============Connected to the database.=================");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("==============Failed to connect to the database.=================");
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}