package com.csce331.requests;

/**
 * Request body carrying a user's Google ID.
 *
 * @param googleId the user's Google ID
 */
public record GoogleIdRequest(String googleId) {
    /**
     * Creates the request, rejecting a missing Google ID.
     *
     * @param googleId the user's Google ID
     * @throws IllegalArgumentException if the Google ID is null or empty
     */
    public GoogleIdRequest {
        if (googleId == null || googleId.isEmpty()) {
            throw new IllegalArgumentException("Google ID cannot be null or empty");
        }
    }
}
