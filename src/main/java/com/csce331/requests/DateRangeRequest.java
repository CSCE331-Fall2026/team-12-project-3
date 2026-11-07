package com.csce331.requests;

import java.time.LocalDateTime;

public record DateRangeRequest(LocalDateTime start, LocalDateTime end) {
    public DateRangeRequest {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
