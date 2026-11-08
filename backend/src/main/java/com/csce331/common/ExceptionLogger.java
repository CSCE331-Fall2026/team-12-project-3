package com.csce331.common;

/**
 * Logs exceptions to standard error.
 */
public class ExceptionLogger {
    /**
     * Prevents instantiation; the logger is used through its static method.
     */
    private ExceptionLogger() {
    }

    /**
     * Writes the exception's message to standard error as one line.
     *
     * @param e the exception to log
     */
    public static void logException(Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}
