package com.csce331.requests;

public record GoogleIdRequest(String googleId) {
    public GoogleIdRequest {
        if (googleId == null || googleId.isEmpty()) {
            throw new IllegalArgumentException("Google ID cannot be null or empty");
        }
    }
}
