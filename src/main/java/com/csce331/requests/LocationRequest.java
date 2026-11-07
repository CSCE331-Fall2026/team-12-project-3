package com.csce331.request;

public record LocationRequest(String location) {
    public LocationRequest {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
    }
}
