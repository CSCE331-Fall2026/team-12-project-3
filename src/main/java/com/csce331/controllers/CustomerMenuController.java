package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;

import com.csce331.models.MenuItem;
import com.csce331.customerbackend.OrderManager;

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
    public static void popularItems(Context ctx) {
        try {
            List<MenuItem> popularItems = orderManager.getPopularItems();
            ctx.status(200).json(popularItems);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
