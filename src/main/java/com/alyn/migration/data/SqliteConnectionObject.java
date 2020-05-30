package com.alyn.migration.data;

import com.alyn.migration.contract.ConnectionObject;

import java.sql.Connection;
import java.sql.DriverManager;

public class SqliteConnectionObject implements ConnectionObject {
    private String url;

    public SqliteConnectionObject(String url) {
        this.url = url;
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            long start = System.currentTimeMillis();
            conn = DriverManager.getConnection(this.url);
            long end = System.currentTimeMillis();
            long elapsed = end - start;
            System.out.println("Getting connection from DriverManager took " + elapsed + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conn;
    }
}
