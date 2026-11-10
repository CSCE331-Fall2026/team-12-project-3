package com.csce331.controllers;

import java.sql.SQLException;

import java.util.List;

import io.javalin.http.Context;

import com.csce331.managers.InventoryManager;
import com.csce331.models.Ingredient;
import com.csce331.models.Utensil;
import com.csce331.models.InventoryLocation;
import com.csce331.models.IngredientRestockData;
import com.csce331.requests.LocationRequest;
import com.csce331.requests.RestockRequest;

/**
 * Handles the inventory HTTP endpoints: ingredients and utensils by storage
 * location, restocking, restock thresholds, end of day, and restock reports.
 *
 * <p>Each method takes the Javalin request context, delegates the work to an
 * {@link InventoryManager}, and writes the status code and body of the
 * response itself. Query failures are reported to the client as a 500 with
 * the error message rather than being thrown.
 */
public class InventoryController {
    private static final InventoryManager inventoryManager = new InventoryManager();

    /**
     * Prevents instantiation; the handlers are all static.
     */
    private InventoryController() {
    }

    /**
     * Responds with every ingredient in the inventory as JSON.
     *
     * <p>Responds 500 with an error message if the database query fails.
     *
     * @param ctx the request context
     */
    public static void allIngredients(Context ctx) {
        try {
            List<Ingredient> ingredients = inventoryManager.getIngredients();
            ctx.status(200).json(ingredients);
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }

    /**
     * Responds with the ingredients stored in the location named in the path.
     * A path of "utensils" returns every utensil instead.
     *
     * <p>Responds 400 for an unknown location and 500 if the database query
     * fails.
     *
     * @param ctx the request context
     */
    public static void ingredientsByLocation(Context ctx) {
        try {
            LocationRequest request = ctx.bodyAsClass(LocationRequest.class);

            if (request.location() == InventoryLocation.UTENSILS) {
                List<Utensil> utensils = inventoryManager.getUtensils();
                ctx.status(200).json(utensils);
                return;
            }

            List<Ingredient> ingredients = inventoryManager.getIngredients(request.location());
            ctx.status(200).json(ingredients);
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(500).result("Error retrieving ingredients: " + e.getMessage());
        }
    }
    
    /**
     * Adds stock to the item named in the path. The amount comes from the
     * {@code quantity} query parameter, and zero is used when it is missing.
     * A {@code isUtensil} query parameter of true restocks a utensil;
     * otherwise an ingredient is restocked.
     *
     * <p>Responds 201 when the ingredient's today-dated stock row is created
     * and 200 when existing stock is increased or a utensil is updated.
     * Responds 400 for a non-numeric quantity and 500 if the database update
     * fails.
     *
     * @param ctx the request context
     */
    public static void restock(Context ctx) {
        try {
            RestockRequest request = ctx.bodyAsClass(RestockRequest.class);
            if (request.isUtensil()) {
                inventoryManager.restockUtensils(request.name(), request.quantity());
                ctx.status(200);
                return;
            }
            ctx.status(inventoryManager.restockIngredients(request.name(), request.quantity()) ? 201 : 200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid quantity format: " + e.getMessage());
        }
    }

    /**
     * Sets the restock threshold of the item named in the path to the
     * {@code threshold} query parameter. A {@code isUtensil} query parameter
     * of true updates a utensil; otherwise an ingredient is updated.
     *
     * <p>Responds 200 on success, 400 for a non-numeric threshold and 500 if
     * the database update fails.
     *
     * @param ctx the request context
     */
    public static void setRestockThreshold(Context ctx) {
        try {
            RestockRequest request = ctx.bodyAsClass(RestockRequest.class);
            if (request.isUtensil()) {
                inventoryManager.setUtensilRestockThreshold(request.name(), request.quantity());
            } else {
                inventoryManager.setIngredientRestockThreshold(request.name(), request.quantity());
            }
            ctx.status(200);
        } catch (SQLException e) {
            ctx.status(500).result("Error restocking ingredients: " + e.getMessage());
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid threshold format: " + e.getMessage());
        }
    }

    /**
     * Ends the current day by deleting stock that has outlived its shelf life.
     *
     * <p>Responds 204 when nothing was deleted and 200 with the number of
     * deleted records otherwise, or 500 if the delete fails.
     *
     * @param ctx the request context
     */
    public static void endDay(Context ctx) {
        try {
            int deletionCount = inventoryManager.endDay();
            if (deletionCount == 0) {
                ctx.status(204);
            } else {
                ctx.status(200).result(Integer.toString(deletionCount));
            }
        } catch (SQLException e) {
            ctx.status(500).result("Error deleting expired ingredients: " + e.getMessage());
        }
    }

    /**
     * Responds with the restock report for the location named in the path, or
     * for every utensil when the path is "utensils". The report lists the items
     * at or below their restock threshold.
     *
     * <p>Responds 400 for an unknown location and 500 if the database query
     * fails.
     *
     * @param ctx the request context
     */
    public static void restockReport(Context ctx) {
        try {
            LocationRequest request = ctx.bodyAsClass(LocationRequest.class);
            if (request.location() == InventoryLocation.UTENSILS) {
                List<Utensil> report = inventoryManager.utensilRestockReport();
                ctx.status(200).json(report);
                return;
            }
            List<IngredientRestockData> report = inventoryManager.ingredientRestockReport(request.location());
            ctx.status(200).json(report);
        } catch (SQLException e) {
            ctx.status(500).result("Error finding restock report: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }
}
