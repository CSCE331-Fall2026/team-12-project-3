package com.csce331.cashierbackend;

import com.csce331.common.DatabaseSetup;
import com.csce331.common.ExceptionLogger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Provides JDBC connections to the cashier backend.
 */
public class Database {

    /**
     * Private constructor to prevent instantiation.
     */
    private Database() {}
    
    /**
     * Opens a new connection to the application database.
     *
     * @return a new database connection
     * @throws SQLException if the connection cannot be established
     */
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseSetup.url, DatabaseSetup.user, DatabaseSetup.pswd);
    }

    /**
     * Safely closes an open database connection if it is non-null and active.
     *
     * @param conn the {@link Connection} to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                ExceptionLogger.logException(e);
            }
        }
    }
}