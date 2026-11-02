package com.csce331.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * An abstract class that provides a connection to the database for its subclasses.
 *
 * @author Kedar Panchal
 */
public abstract class DatabaseConnector {
    /** The open JDBC connection used by subclasses. */
    private Connection connection;

    /**
     * Constructs a new {@code DatabaseConnector} and establishes a connection to the database.
     */
    public DatabaseConnector() {
        try {
            this.connection = DriverManager.getConnection(
                    DatabaseSetup.url, DatabaseSetup.user, DatabaseSetup.pswd);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }

    /**
     * Creates a new {@code Statement} object for executing SQL queries.
     *
     * @return a new {@code Statement} object
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
     * Closes the connection to the database.
     *
     * @return {@code true} if the connection was closed successfully, {@code false} otherwise
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
