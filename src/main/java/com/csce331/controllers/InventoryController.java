package com.csce331.controllers;


import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;

import com.csce331.managerbackend.InventoryManager;
import com.csce331.managerbackend.Ingredient;
import com.csce331.managerbackend.Utensil;
import com.csce331.managerbackend.InventoryLocation;
import com.csce331.managerbackend.IngredientRestockData;

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
        String inventoryType = ctx.pathParam("location");
        try {
            if (inventoryType.equals("utensils")) {
                List<Utensil> utensils = inventoryManager.getUtensils();
                ctx.status(200).json(utensils);
                return;
            }

            InventoryLocation location = parseLocation(inventoryType);
            List<Ingredient> ingredients = inventoryManager.getIngredientsByLocation(location);
            ctx.status(200).json(ingredients);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }
    
    public static void restock(Context ctx) {
        String name = ctx.pathParam("name");
        try {
            String quantityParam = ctx.queryParam("quantity");
            int quantity = quantityParam != null ? Integer.parseInt(quantityParam) : 0;
            String isUtensilParam = ctx.queryParam("isUtensil");
            boolean isUtensil = isUtensilParam != null && isUtensilParam.equalsIgnoreCase("true");
            
            if (isUtensil) {
                inventoryManager.restockUtensils(name, quantity);
                ctx.status(200);
                return;
            }
            ctx.status(inventoryManager.restockIngredients(name, quantity) ? 201 : 200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid quantity format: " + e.getMessage());
        }
    }

    public static void setRestockThreshold(Context ctx) {
        String name = ctx.pathParam("name");
        String isUtensilParam = ctx.queryParam("isUtensil");
        boolean isUtensil = isUtensilParam != null && isUtensilParam.equalsIgnoreCase("true");
        try {

            int threshold = Integer.parseInt(ctx.queryParam("threshold"));
            if (isUtensil) {
                inventoryManager.setUtensilRestockThreshold(name, threshold);
            } else {
                inventoryManager.setIngredientRestockThreshold(name, threshold);
            }
            ctx.status(200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid threshold format: " + e.getMessage());
        }
    }

    public static void endDay(Context ctx) {
        try {
            int deletionCount = inventoryManager.endDay();
            if (deletionCount == 0) {
                ctx.status(204);
            } else {
                ctx.status(200).result(deletionCount);
            }
        } catch (SQLException e) {
            ctx.status(500).result("Error deleting expired ingredients: " + e.getMessage());
        }
    }

    public static void restockReport(Context ctx) {
        String inventoryType = ctx.pathParam("location");
        try {
            if (inventoryType.equals("utensils")) {
                List<Utensil> report = inventoryManager.utensilRestockReport();
                ctx.status(200).json(report);
                return;
            }
            InventoryLocation location = parseLocation(inventoryType);
            List<IngredientRestockData> report = inventoryManager.ingredientRestockReport(location);
            ctx.status(200).json(report);
        } catch (SQLException e) {
            ctx.status(500).result("Error finding restock report: " + e.getMessage());
        } catch (IllegalArugumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }
}
