package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import java.time.LocalDate;

import io.javalin.http.Context;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.models.MenuItemSize;
import com.csce331.requests.DateRangeRequest;
import com.csce331.requests.CategoryRequest;
import com.csce331.requests.PriceUpdateRequest;
import com.csce331.requests.DiscontinueRequest;
import com.csce331.managerbackend.MenuItemManager;

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
