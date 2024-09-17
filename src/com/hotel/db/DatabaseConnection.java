package com.hotel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5434/postgres";
    private static final String USER = "user";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        try {
            // Print out connection details (remove in production)
            System.out.println("Attempting to connect to: " + URL);
            System.out.println("With user: " + USER);

            // Load the driver explicitly
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found!");
            e.printStackTrace();
            throw new SQLException("PostgreSQL JDBC driver not found!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database:");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }
}