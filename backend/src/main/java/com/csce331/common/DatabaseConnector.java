package com.csce331.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Base class for managers that work with the restaurant database and need a
 * shared JDBC connection.
 *
 * <p>Creating a connector opens a connection using the credentials in
 * the environment variables. If the database cannot be reached, the error is
 * logged and the process exits, so a Manager is never left with a dead 
 * connection. Subclasses can get the open connection from 
 * {@link #getConnection()} when they need prepared statements or transactions.
 *
 * @author Kedar Panchal
 */
public abstract class DatabaseConnector {
    private Connection connection;

    /**
     * Opens a connection to the database.
     *
     * <p>If the connection fails, the error is logged and the process exits.
     */
    public DatabaseConnector() {
        String databaseURL = System.getenv("DATABASE_URL");
        String databaseUser = System.getenv("DATABASE_USER");
        String databasePassword = System.getenv("DATABASE_PASSWORD");
        try {
            this.connection = DriverManager.getConnection(
                    databaseURL, databaseUser, databasePassword);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }

    /**
     * Creates a statement for running SQL against the database.
     *
     * <p>If the statement cannot be created, the error is logged and the process
     * exits, so this method never returns {@code null}.
     *
     * @return a new statement
     */
    public Statement createStatement() {
        try {
            return this.connection.createStatement();
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
        // Never reached, but required to satisfy the compiler
        return null;
    }

    /**
     * Returns the connection held by this connector, for subclasses that need prepared
     * statements or transaction control.
     *
     * @return the open JDBC connection
     */
    protected Connection getConnection() {
        return this.connection;
    }

    /**
     * Closes the database connection. Errors are logged rather than thrown.
     *
     * @return {@code true} if the connection closed, {@code false} otherwise
     */
    public boolean closeConnection() {
        try {
            this.connection.close();
            return true;
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            return false;
        }
    }
}
