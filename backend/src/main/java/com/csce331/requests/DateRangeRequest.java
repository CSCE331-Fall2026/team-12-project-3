package com.csce331.requests;

import java.time.LocalDateTime;

/**
 * Request body giving the date and time range a sales report covers.
 *
 * @param start the first date and time in the range
 * @param end the last date and time in the range
 */
public record DateRangeRequest(LocalDateTime start, LocalDateTime end) {
    /**
     * Creates the request, rejecting a range that ends before it starts.
     *
     * @param start the first date and time in the range
     * @param end the last date and time in the range
     * @throws IllegalArgumentException if {@code start} is after {@code end}
     */
    public DateRangeRequest {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
