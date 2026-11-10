package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.requests.CategoryRequest;
import com.csce331.requests.OrderRequest;
import com.csce331.managers.OrderManager;

/**
 * HTTP handlers for browsing the customer menu.
 */
public class CustomerMenuController {
    private static final OrderManager orderManager = new OrderManager();

    /**
     * Prevents instantiation; the handlers are all static.
     */
    private CustomerMenuController() {
    }

    /**
     * Responds with the most popular active menu items.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/customer/menuitems",
        methods = HttpMethod.GET,
        operationId = "getPopularMenuItems",
        summary = "List the most popular active menu items",
        description = "Returns the most popular active menu items for customers "
            + "to browse.",
        tags = {"Customer menu"},
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The most popular active menu items.",
                content = @OpenApiContent(from = MenuItem[].class)
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void popularItems(Context ctx) {
        try {
            List<MenuItem> popularItems = orderManager.getPopularItems();
            ctx.status(200).json(popularItems);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    /**
     * Responds with the active menu items in the requested category. Responds
     * 400 for an invalid category and 500 if the database query fails.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/customer/menuitems",
        methods = HttpMethod.POST,
        operationId = "getCustomerMenuItemsByCategory",
        summary = "List active menu items in a category",
        description = "Returns the active menu items in the requested category. "
            + "Responds with 400 for an invalid category and 500 if the database "
            + "query fails. The handler is also registered for HTTP QUERY; POST is "
            + "documented here because OpenAPI 3.1 cannot describe QUERY.",
        tags = {"Customer menu"},
        requestBody = @OpenApiRequestBody(
            description = "The menu category to filter by.",
            required = true,
            content = @OpenApiContent(from = CategoryRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Active menu items in the requested category.",
                content = @OpenApiContent(from = MenuItem[].class)
            ),
            @OpenApiResponse(
                status = "400",
                description = "The category is missing or invalid.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void menuItemByCategory(Context ctx) {
        try {
            CategoryRequest request = ctx.bodyAsClass(CategoryRequest.class);
            List<MenuItem> menuItems = orderManager.getCategoryItems(request.category());
            ctx.status(200).json(menuItems);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid category: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    /**
     * Places the order in the request body. Responds 201 on success, 400 for
     * an invalid order, and 500 if the database query fails.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/customer/order",
        methods = HttpMethod.POST,
        operationId = "placeOrder",
        summary = "Place a customer order",
        description = "Places the order described in the request body. Responds "
            + "with 201 on success, 400 for an invalid order, and 500 if the "
            + "database query fails.",
        tags = {"Customer menu"},
        requestBody = @OpenApiRequestBody(
            description = "The ordered menu items and their quantities, the "
                + "payment method, and the total non-negative price.",
            required = true,
            content = @OpenApiContent(from = OrderRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "201",
                description = "The order was placed."
            ),
            @OpenApiResponse(
                status = "400",
                description = "The order is invalid.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void order(Context ctx) {
        try {
            OrderRequest request = ctx.bodyAsClass(OrderRequest.class);
            orderManager.order(request.items(), request.method(), request.price());
            ctx.status(201);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid order: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
