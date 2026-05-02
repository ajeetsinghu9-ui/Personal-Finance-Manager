package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton utility class for JDBC MySQL connection.
 */
public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/finance_manager";
    private static final String USER     = "root";
    private static final String PASSWORD = "PASSword&1313";          // <-- change to your MySQL password

    private static Connection connection = null;

    private DBConnection() {}

    /**
     * Returns a single shared Connection (creates one if needed).
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }

    /** Close the connection when the application exits. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
