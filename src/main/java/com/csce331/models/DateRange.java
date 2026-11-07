package com.csce331.models;

import java.time.LocalDateTime;

public record DateRange(LocalDateTime start, LocalDateTime end) {
    public DateRange {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }
}
