package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.UserRole;

@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record NewUserRequest(String email, String googleId, UserRole role) {
    public NewUserRequest {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (googleId == null || googleId.isEmpty()) {
            throw new IllegalArgumentException("Google ID cannot be null or empty");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
    }
}
