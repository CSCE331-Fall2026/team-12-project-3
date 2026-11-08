package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.requests.CategoryRequest;
import com.csce331.requests.OrderRequest;
import com.csce331.customerbackend.OrderManager;

/**
 * HTTP handlers for browsing the customer menu.
 */
public class CustomerMenuController {
    private static final OrderManager orderManager = new OrderManager();

    /**
     * Responds with the most popular active menu items.
     *
     * @param ctx the Javalin request context
     */
    public static void popularItems(Context ctx) {
        try {
            List<MenuItem> popularItems = orderManager.getPopularItems();
            ctx.status(200).json(popularItems);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

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

    public static void order(Context ctx) {
        try {
            OrderRequest request = ctx.bodyAsClass(OrderRequest.class);
            orderManager.placeOrder(request.items(), request.method(), request.price());
            ctx.status(201);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid order: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
