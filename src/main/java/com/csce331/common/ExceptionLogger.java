package com.csce331.common;

/**
 * Utility class for logging exceptions in a consistent manner.
 */
public class ExceptionLogger {
    /** Private constructor to prevent instantiation. */
    private ExceptionLogger() {
    }

    /**
     * Logs the given exception as one short line on standard error,
     * so the terminal output stays minimal.
     *
     * @param e the exception to log
     */
    public static void logException(Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}
