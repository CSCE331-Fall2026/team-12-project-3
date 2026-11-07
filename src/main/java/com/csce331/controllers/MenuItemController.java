package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import java.time.LocalDate;

import io.javalin.http.Context;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.models.MenuItemSize;
import com.csce331.models.DateRange;
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
        String categoryParam = ctx.pathParam("category");
        try {
            MenuItemCategory category = MenuItemCategory.fromString(categoryParam);
            List<MenuItem> menuItems = menuItemManager.getMenuItems(category);
            ctx.status(200).json(menuItems);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid category: " + categoryParam);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }

    public static void updatePrice(Context ctx) {
        String name = ctx.pathParam("name");
        String sizeParam = ctx.queryParam("size");
        String newPriceParam = ctx.queryParam("newPrice");
        try {
            MenuItemSize size = sizeParam == null ? MenuItemSize.MEDIUM : MenuItemSize.fromString(sizeParam);
            double newPrice = Double.parseDouble(newPriceParam);
            menuItemManager.updatePrice(name, size, newPrice);
            ctx.status(200);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid size: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid price format: " + newPriceParam);
        } catch (SQLException e) {
            ctx.status(500).result("Database update failed: " + e.getMessage());
        }
    }

    public static void discontinueMenuItem(Context ctx) {
        String name = ctx.pathParam("name");
        String sizeParam = ctx.queryParam("size");
        try {
            MenuItemSize size = sizeParam == null ? MenuItemSize.MEDIUM : MenuItemSize.fromString(sizeParam);
            menuItemManager.discontinueMenuItem(name, size);
            ctx.status(200);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Invalid size: " + e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Database update failed: " + e.getMessage());
        }
    }

    public static void salesReport(Context ctx) {
        try {
            DateRange dates = ctx.bodyAsClass(DateRange.class);
            Map<LocalDate, Map<String, Double>> salesReport = menuItemManager.getSalesReport(dates.start(), dates.end());
            ctx.status(200).json(salesReport);
        } catch (SQLException e) {
            ctx.status(500).result("Database query failed: " + e.getMessage());
        }
    }
}
