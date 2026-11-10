package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import java.time.LocalDate;

import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;
import io.javalin.openapi.OpenApiContent;
import io.javalin.openapi.OpenApiExampleProperty;
import io.javalin.openapi.OpenApiRequestBody;
import io.javalin.openapi.OpenApiResponse;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.models.MenuItemSize;
import com.csce331.requests.DateRangeRequest;
import com.csce331.requests.CategoryRequest;
import com.csce331.requests.PriceUpdateRequest;
import com.csce331.requests.DiscontinueRequest;
import com.csce331.managers.MenuItemManager;

/**
 * HTTP handlers for browsing the menu, updating prices, and discontinuing
 * items.
 */
public class MenuItemController {
    private static final MenuItemManager menuItemManager = new MenuItemManager();

    /**
     * Prevents instantiation; the handlers are all static.
     */
    private MenuItemController() {
    }

    /**
     * Responds with every active menu item.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/manager/menuitems",
        methods = HttpMethod.GET,
        operationId = "getAllMenuItems",
        summary = "List every active menu item",
        description = "Returns every active menu item, including its category, "
            + "size, price, image URL, and availability.",
        tags = {"Menu"},
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Every active menu item.",
                content = @OpenApiContent(from = MenuItem[].class)
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void allMenuItems(Context ctx) {
        try {
            List<MenuItem> menuItems = menuItemManager.getMenuItems();
            ctx.status(200).json(menuItems);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    /**
     * Responds with the active menu items in the requested category.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/manager/menuitems",
        methods = HttpMethod.POST,
        operationId = "getMenuItemsByCategory",
        summary = "List active menu items in a category",
        description = "Returns the active menu items in the requested category. "
            + "The handler is also registered for HTTP QUERY; POST is documented "
            + "here because OpenAPI 3.1 cannot describe QUERY.",
        tags = {"Menu"},
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
            List<MenuItem> menuItems = menuItemManager.getMenuItems(request.category());
            ctx.status(200).json(menuItems);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid category: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    /**
     * Updates the price of the menu item named in the request body.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/manager/menuitems/price",
        methods = HttpMethod.PATCH,
        operationId = "updateMenuItemPrice",
        summary = "Update a menu item's price",
        description = "Updates the price of the menu item named in the request "
            + "body.",
        tags = {"Menu"},
        requestBody = @OpenApiRequestBody(
            description = "The menu item to update, its portion size, and its "
                + "new non-negative price.",
            required = true,
            content = @OpenApiContent(from = PriceUpdateRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The price was updated."
            ),
            @OpenApiResponse(
                status = "400",
                description = "The price is not a number, or the size is invalid.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database update failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void updatePrice(Context ctx) {
        try {
            PriceUpdateRequest request = ctx.bodyAsClass(PriceUpdateRequest.class);
            menuItemManager.updatePrice(request.name(), request.size(), request.price());
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid price format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid size: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database update failed: " + e.getMessage());
        }
    }

    /**
     * Marks the menu item named in the request body as discontinued.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/manager/menuitems/discontinue",
        methods = HttpMethod.PATCH,
        operationId = "discontinueMenuItem",
        summary = "Discontinue a menu item",
        description = "Marks the menu item named in the request body as "
            + "discontinued so that it is no longer offered.",
        tags = {"Menu"},
        requestBody = @OpenApiRequestBody(
            description = "The menu item to discontinue and its portion size.",
            required = true,
            content = @OpenApiContent(from = DiscontinueRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "The menu item was marked as discontinued."
            ),
            @OpenApiResponse(
                status = "400",
                description = "The request body is invalid or the size is unknown.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database update failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void discontinueMenuItem(Context ctx) {
        try {
            DiscontinueRequest request = ctx.bodyAsClass(DiscontinueRequest.class);
            menuItemManager.discontinueMenuItem(request.name(), request.size());
            ctx.status(200);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid size: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database update failed: " + e.getMessage());
        }
    }

    /**
     * Responds with the revenue each menu item earned, grouped by sale date,
     * for the date range in the request body.
     *
     * @param ctx the Javalin request context
     */
    @OpenApi(
        path = "/manager/salesreport",
        methods = HttpMethod.GET,
        operationId = "getSalesReport",
        summary = "Get revenue by menu item and sale date",
        description = "Returns the revenue each menu item earned, grouped by "
            + "sale date, for the date range in the request body. The response "
            + "is an object keyed by date (yyyy-MM-dd), then by menu item name, "
            + "with the revenue as the value.",
        tags = {"Menu"},
        requestBody = @OpenApiRequestBody(
            description = "The first and last date and time to include in the "
                + "report.",
            required = true,
            content = @OpenApiContent(from = DateRangeRequest.class)
        ),
        responses = {
            @OpenApiResponse(
                status = "200",
                description = "Revenue keyed by sale date and then by menu item name.",
                content = @OpenApiContent(
                    from = Map.class,
                    exampleObjects = {
                        @OpenApiExampleProperty(
                            name = "2026-11-01",
                            objects = {
                                @OpenApiExampleProperty(name = "Cheeseburger", raw = "120.5")
                            }
                        )
                    }
                )
            ),
            @OpenApiResponse(
                status = "500",
                description = "The database query failed.",
                content = @OpenApiContent(from = String.class, mimeType = "text/plain")
            )
        }
    )
    public static void salesReport(Context ctx) {
        try {
            DateRangeRequest dates = ctx.bodyAsClass(DateRangeRequest.class);
            Map<LocalDate, Map<String, Double>> salesReport = menuItemManager.salesReport(dates.start(), dates.end());
            ctx.status(200).json(salesReport);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
