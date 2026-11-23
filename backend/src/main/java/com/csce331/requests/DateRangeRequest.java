package com.csce331.requests;

/**
 * Request body giving the date and time range a sales report covers.
 *
 * @param start the first date and time in ISO-8601 format
 * @param end the last date and time in ISO-8601 format
 */
public record DateRangeRequest(String start, String end) {
    /**
     * Creates the request, rejecting a missing date or time.
     *
     * @param start the first date and time in ISO-8601 format
     * @param end the last date and time in ISO-8601 format
     * @throws IllegalArgumentException if either value is missing
     */
    public DateRangeRequest {
        if (start == null || start.isBlank() || end == null || end.isBlank()) {
            throw new IllegalArgumentException("Start and end date-times are required");
        }
    }
}
