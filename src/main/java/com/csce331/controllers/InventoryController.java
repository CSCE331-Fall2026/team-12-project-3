package com.csce331.controllers;


import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;

import com.csce331.managerbackend.InventoryManager;
import com.csce331.managerbackend.Ingredient;
import com.csce331.managerbackend.InventoryLocation;

public class InventoryController {
    private static InventoryManager inventoryManager = new InventoryManager();

    private static InventoryLocation parseLocation(String inventoryType) {
        switch (inventoryType.toLowerCase()) {
            case "freezer" -> {
                return InventoryLocation.FREEZER;
            }
            case "refrigerator" -> {
                return InventoryLocation.REFRIGERATOR;
            }
            case "pantry" -> {
                return InventoryLocation.PANTRY;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid inventory type: " + inventoryType);
        }
    }

    public static void allIngredients(Context ctx) {
        try {
            List<Ingredient> ingredients = inventoryManager.getIngredients();
            ctx.status(200).json(ingredients);
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }

    public static void ingredientsByLocation(Context ctx) {
        String inventoryType = ctx.pathParam("inventoryLocation");
        try {
            InventoryLocation location = parseLocation(inventoryType);
            List<Ingredient> ingredients = inventoryManager.getIngredientsByLocation(location);
            ctx.status(200).json(ingredients);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }

    public static void restock(Context ctx) {
        try {
            String name = ctx.pathParam("name");
            int quantity = Integer.parseInt(ctx.queryParam("quantity"));
            inventoryManager.restock(name, quantity);
            // TODO: Return a 201 if a new inventory item was created
            ctx.status(200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid quantity format: " + e.getMessage());
        }
    }
}
