package com.csce331.controllers;

import java.sql.SQLException;

import java.util.Optional;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

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
    @OpenApi(
        path = "/auth/role/email",
        methods = HttpMethod.POST,
        operationId = "getUserRoleByEmail",
        summary = "Look up a user's role by email",
        description = "Returns the role of the user whose email is in the request "
            + "body. Responds with 200 and the role, 404 if no user has that email, "
            + "400 for an invalid request, and 500 if the database query fails. "
            + "The handler is also registered for HTTP QUERY; POST is documented "
            + "here because OpenAPI 3.1 cannot describe QUERY.",
        tags = {"Authentication"},
        requestBody = @OpenApiRequestBody(
            description = "The email address of the user to look up.",
            required = true,
            content = @OpenApiContent(from = EmailRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The role assigned to the user.",
                content = @OpenApiContent(from = UserRole.class)
            ),
            @OpenApiResponse(
                status = "404",
                description = "No user has the requested email address.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "400",
                description = "The email is missing or invalid.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
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
    @OpenApi(
        path = "/auth/role/id",
        methods = HttpMethod.POST,
        operationId = "getUserRoleById",
        summary = "Look up a user's role by Google ID",
        description = "Returns the role of the user whose Google ID is in the "
            + "request body. Responds with 200 and the role, 404 if no user has "
            + "that ID, 400 for an invalid request, and 500 if the database query "
            + "fails. The handler is also registered for HTTP QUERY; POST is "
            + "documented here because OpenAPI 3.1 cannot describe QUERY.",
        tags = {"Authentication"},
        requestBody = @OpenApiRequestBody(
            description = "The Google ID of the user to look up.",
            required = true,
            content = @OpenApiContent(from = GoogleIdRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The role assigned to the user.",
                content = @OpenApiContent(from = UserRole.class)
            ),
            @OpenApiResponse(
                status = "404",
                description = "No user has the requested Google ID.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "400",
                description = "The Google ID is missing or invalid.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
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
    @OpenApi(
        path = "/auth/newuser",
        methods = HttpMethod.POST,
        operationId = "createUser",
        summary = "Register a new user",
        description = "Registers the user described in the request body. Responds "
            + "with 201 when the user is created, 409 if the email is already "
            + "registered, 400 for an invalid request, and 500 if the database "
            + "update fails.",
        tags = {"Authentication"},
        requestBody = @OpenApiRequestBody(
            description = "The user's email address, Google ID, and role.",
            required = true,
            content = @OpenApiContent(from = NewUserRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "201",
                description = "The user was created.",
                content = @OpenApiContent(
                    from = String.class,
                    mimeType = "text/plain",
                    example = "User created successfully"
                )
            ),
            @OpenApiResponse(
                status = "409",
                description = "A user with this email address is already registered.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "400",
                description = "The request body is missing required information.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database update failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
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
