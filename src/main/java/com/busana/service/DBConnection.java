package com.busana.service;

import org.springframework.stereotype.Component;

@Component
public class DBConnection {
    private static DBConnection instance;

    private DBConnection() {
       System.out.println("DBConnection instance created.");
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
}