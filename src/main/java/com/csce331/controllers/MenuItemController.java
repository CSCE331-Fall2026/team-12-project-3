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

public class MenuItemController {
    private static final MenuItemManager menuItemManager = new MenuItemManager();

    public static void allMenuItems(Context ctx) {
        try {
            List<MenuItem> menuItems = menuItemManager.getMenuItems();
            ctx.status(200).json(menuItems);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    public static void menuItemByCategory(Context ctx) {
        try {
            CategoryRequest request = ctx.bodyAsClass(CategoryRequest.class);
            List<MenuItem> menuItems = menuItemManager.getMenuItems(request.category());
            ctx.status(200).json(menuItems);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid category: " + categoryParam);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    public static void updatePrice(Context ctx) {
        try {
            PriceUpdateRequest request = ctx.bodyAsClass(PriceUpdateRequest.class);
            menuItemManager.updatePrice(request.name(), request.size(), request.price());
            ctx.status(200);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid price format: " + newPriceParam);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid size: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database update failed: " + e.getMessage());
        }
    }

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

    public static void salesReport(Context ctx) {
        try {
            DateRangeRequest dates = ctx.bodyAsClass(DateRange.class);
            Map<LocalDate, Map<String, Double>> salesReport = menuItemManager.salesReport(dates.start(), dates.end());
            ctx.status(200).json(salesReport);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
