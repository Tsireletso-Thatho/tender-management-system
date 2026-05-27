package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing database connections using JNDI DataSource.
 * Provides a centralized method for obtaining database connections from the connection pool.
 * 
 * CRITICAL: Uses Tomcat JNDI DataSource configured in context.xml.
 * No DriverManager.getConnection() calls anywhere in the application.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class DatabaseConnection {
    
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String JNDI_NAME = "java:/comp/env/jdbc/ProcureGovDB";
    
    private static DataSource dataSource;
    
    // Static initializer to load the DataSource once
    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DataSource: {0}", e.getMessage());
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private DatabaseConnection() {
    }
    
    /**
     * Initializes the JNDI DataSource.
     * Called once during class loading.
     * 
     * @throws NamingException if JNDI lookup fails
     */
    private static void initializeDataSource() throws NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        dataSource = (DataSource) envContext.lookup("jdbc/ProcureGovDB");
        LOGGER.log(Level.INFO, "DataSource initialized successfully: {0}", JNDI_NAME);
    }
    
    /**
     * Gets a database connection from the connection pool.
     * 
     * @return Connection object from the pool
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            try {
                initializeDataSource();
            } catch (NamingException e) {
                LOGGER.log(Level.SEVERE, "Failed to reinitialize DataSource: {0}", e.getMessage());
                throw new SQLException("Database connection pool is not available", e);
            }
        }
        
        Connection conn = dataSource.getConnection();
        LOGGER.log(Level.FINE, "Database connection obtained from pool");
        return conn;
    }
    
    /**
     * Safely closes a database connection, returning it to the pool.
     * 
     * @param connection the Connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.log(Level.FINE, "Database connection returned to pool");
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection: {0}", e.getMessage());
            }
        }
    }
    
    /**
     * Tests if the database connection pool is available.
     * 
     * @return true if a connection can be obtained, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Database connection test failed: {0}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the current DataSource for direct access if needed.
     * 
     * @return the configured DataSource
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                initializeDataSource();
            } catch (NamingException e) {
                LOGGER.log(Level.SEVERE, "Failed to get DataSource: {0}", e.getMessage());
            }
        }
        return dataSource;
    }
}