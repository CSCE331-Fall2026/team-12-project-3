package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.csce331.models.UserRole;

/**
 * Request body for registering a new user.
 *
 * @param email the user's email address
 * @param googleId the user's Google ID
 * @param role the role to assign the user
 */
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record NewUserRequest(String email, String googleId, UserRole role) {
    /**
     * Creates the request, rejecting a missing email, Google ID, or role.
     *
     * @param email the user's email address
     * @param googleId the user's Google ID
     * @param role the role to assign the user
     * @throws IllegalArgumentException if the email or Google ID is null or
     *         empty, or the role is null
     */
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
