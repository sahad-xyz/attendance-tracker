package org.example;

import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("✅ Connected to the database successfully!");
        } else {
            System.out.println("❌ Connection failed.");
        }
    }
}