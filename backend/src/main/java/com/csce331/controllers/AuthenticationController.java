package com.csce331.controllers;

import java.sql.SQLException;

import java.util.Optional;

import io.javalin.http.Context;

import com.csce331.authentication.AuthenticationManager;
import com.csce331.models.UserRole;
import com.csce331.requests.EmailRequest;
import com.csce331.requests.GoogleIdRequest;
import com.csce331.requests.NewUserRequest;

public class AuthenticationController {
    private static final AuthenticationManager authManager = new AuthenticationManager();

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
