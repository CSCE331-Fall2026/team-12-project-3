package com.csce331.requests;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Request body carrying a user's email address.
 *
 * @param email the user's email address
 */
@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
public record EmailRequest(String email) {
    /**
     * Creates the request, rejecting a missing email.
     *
     * @param email the user's email address
     * @throws IllegalArgumentException if the email is null or empty
     */
    public EmailRequest {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }
}
