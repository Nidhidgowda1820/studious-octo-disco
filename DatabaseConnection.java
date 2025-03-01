package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/testdb"; // Replace 'testdb' with your database name.
    private static final String USER = "postgres"; // Replace with your PostgreSQL username.
    private static final String PASSWORD = "Nidhidgowdacse@2027"; // Replace with your PostgreSQL password.

    /**
     * Establishes and returns a connection to the PostgreSQL database.
     *
     * @return A Connection object or null if the connection fails.
     */
    public static Connection getConnection() {
        try {
            // Optional: Load the JDBC driver (newer versions of JDBC auto-register it)
            // Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Closes the provided Connection to release resources.
     *
     * @param connection The Connection to be closed.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection!");
                e.printStackTrace();
            }
        }
    }
}