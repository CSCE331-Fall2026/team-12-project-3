package com.csce331.controllers;

import java.sql.SQLException;

import java.util.Optional;

import io.javalin.http.Context;

import com.csce331.managers.AuthenticationManager;
import com.csce331.models.UserRole;
import com.csce331.requests.EmailRequest;
import com.csce331.requests.GoogleIdRequest;
import com.csce331.requests.NewUserRequest;

/**
 * Handles the authentication HTTP endpoints: looking up a user's role and
 * registering new users.
 *
 * <p>Each method takes the Javalin request context, delegates the work to an
 * {@link AuthenticationManager}, and writes the status code and body of the
 * response itself. Failures are reported to the client as an error status
 * rather than being thrown.
 */
public class AuthenticationController {
    private static final AuthenticationManager authManager = new AuthenticationManager();

    /**
     * Prevents instantiation; the handlers are all static.
     */
    private AuthenticationController() {
    }

    /**
     * Responds with the role of the user whose email is in the request body.
     *
     * <p>Responds 200 with the role, 404 if no user has that email, 400 for an
     * invalid request, and 500 if the database query fails.
     *
     * @param ctx the request context
     */
    public static void getUserRoleByEmail(Context ctx) {
        try {
            EmailRequest request = ctx.bodyAsClass(EmailRequest.class);
            Optional<UserRole> roleOpt = authManager.getUserRoleByEmail(request.email());
            if (roleOpt.isPresent()) {
                ctx.status(200).json(roleOpt.get());
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid email: " + e.getMessage());
        }
    }

    /**
     * Responds with the role of the user whose Google ID is in the request
     * body.
     *
     * <p>Responds 200 with the role, 404 if no user has that ID, 400 for an
     * invalid request, and 500 if the database query fails.
     *
     * @param ctx the request context
     */
    public static void getUserRoleById(Context ctx) {
        try {
            GoogleIdRequest request = ctx.bodyAsClass(GoogleIdRequest.class);
            Optional<UserRole> roleOpt = authManager.getUserRoleById(request.googleId());
            if (roleOpt.isPresent()) {
                ctx.status(200).json(roleOpt.get());
            } else {
                ctx.status(404).result("User not found");
            }
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid Google ID: " + e.getMessage());
        }
    }

    /**
     * Registers the user described in the request body.
     *
     * <p>Responds 201 when the user is created, 409 if the email is already
     * registered, 400 for an invalid request, and 500 if the database update
     * fails.
     *
     * @param ctx the request context
     */
    public static void createUser(Context ctx) {
        try {
            NewUserRequest request = ctx.bodyAsClass(NewUserRequest.class);
            if (authManager.getUserRoleByEmail(request.email()).isPresent()) {
                ctx.status(409).result("User with this email already exists");
                return;
            }
            authManager.createUser(request.email(), request.role(), request.googleId());
            ctx.status(201).result("User created successfully");
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid input: " + e.getMessage());
        }
    }
}
