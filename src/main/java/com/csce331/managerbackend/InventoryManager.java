package com.csce331.managerbackend;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.DatabaseSetup;
import com.csce331.common.ExceptionLogger;

/**
 * Reads and updates the restaurant's inventory: ingredients by storage
 * location, utensils, restock thresholds, expired stock, and sales totals.
 *
 * <p>The manager keeps no inventory of its own; every call reads from or writes
 * to the database. A failed query is logged and ends the program.
 *
 * @author Kedar Panchal
 */
public class InventoryManager extends DatabaseConnector {

    /**
     * Creates a manager connected to the inventory database.
     */
    public InventoryManager() {
        super();
    }

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

    private static Unit parseUnit(String unit) {
        switch (unit.toLowerCase()) {
            case "kilograms" -> {
                return Unit.KILOGRAMS;
            }
            case "liters" -> {
                return Unit.LITERS;
            }
            case "items" -> {
                return Unit.ITEMS;
            }
            default -> throw new IllegalArgumentException("Invalid unit: " + unit);
        }
    }

    /**
     * Returns every ingredient in the inventory, across all locations.
     *
     * @return a list of all ingredients
     */
    public List<Ingredient> getIngredients() {
        ArrayList<Ingredient> allIngredients = new ArrayList<>();

        try {
            Statement ingredientStatement = this.createStatement();
            String ingredientQuery = "SELECT * FROM ingredients;";
            ResultSet ingredientResultSet = ingredientStatement.executeQuery(ingredientQuery);

            while (ingredientResultSet.next()) {
                String name = ingredientResultSet.getString("name");
                InventoryLocation location = this.parseLocation(ingredientResultSet.getString("inventory_type"));
                Unit unit = this.parseUnit(ingredientResultSet.getString("unit"));
                int shelfLife = ingredientResultSet.getInt("shelf_life");
                int restockThreshold = ingredientResultSet.getInt("restock_threshold");

                Ingredient ingredient = new Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .build();
                allIngredients.add(ingredient);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return allIngredients;
    }

    /**
     * Returns the ingredients stored in the given location.
     *
     * @param location the inventory location to query
     * @return a list of ingredients in that location
     */
    public List<Ingredient> getIngredients(InventoryLocation location) {
        ArrayList<Ingredient> ingredientsInLocation = new ArrayList<>();

        try {
            Statement ingredientStatement = this.createStatement();
            String ingredientQuery = "SELECT * FROM ingredients "
                + "WHERE inventory_type = '" + location.toString().toLowerCase() + "';";
            ResultSet ingredientResultSet = ingredientStatement.executeQuery(ingredientQuery);

            while (ingredientResultSet.next()) {
                String name = ingredientResultSet.getString("name");
                Unit unit = this.parseUnit(ingredientResultSet.getString("unit"));
                int shelfLife = ingredientResultSet.getInt("shelf_life");
                int restockThreshold = ingredientResultSet.getInt("restock_threshold");

                Ingredient ingredient = new Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .build();
                ingredientsInLocation.add(ingredient);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        return ingredientsInLocation;
    }
    
    /**
     * Returns every utensil tracked in the inventory.
     *
     * @return a list of all utensils
     */
    public List<Utensil> getUtensils() {
        ArrayList<Utensil> allUtensils = new ArrayList<>();

        try {
            Statement utensilStatement = this.createStatement();
            String utensilQuery = "SELECT * FROM utensils;";
            ResultSet utensilResultSet = utensilStatement.executeQuery(utensilQuery);

            while (utensilResultSet.next()) {
                String name = utensilResultSet.getString("name");
                int restockThreshold = utensilResultSet.getInt("restock_threshold");

                Utensil utensil = new Utensil.Builder(name)
                    .restockThreshold(restockThreshold)
                    .quantity(utensilResultSet.getInt("quantity"))
                    .build();
                allUtensils.add(utensil);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return allUtensils;
    }
    
    /**
     * Adds the given amount to the named ingredient's stock for the current date,
     * in that ingredient's unit. If the ingredient already has an inventory
     * record for today, its quantity is increased; otherwise a record dated today
     * is created. If no ingredient has that name, nothing changes.
     *
     * @param itemName the name of the ingredient to restock
     * @param quantity the amount to add
     * @return true if a new row was created, false otherwise
     */
    public boolean restockIngredients(String itemName, int quantity) {
        try {
            Statement restockStatement = this.createStatement();

            String rowsCount = "SELECT COUNT(*) AS items FROM inventory;";
            ResultSet rowsCountSet = restockStatement.executeQuery(rowsCount);
            rowsCountSet.next();
            int oldRowsCount = rowsCountSet.getInt("items");

            String restockQuery = "INSERT INTO inventory (ingredient_id, quantity, restock_date) "
                + "SELECT id, " + quantity + ", CURRENT_DATE FROM ingredients "
                + "WHERE name = '" + itemName + "' " +
                + "ON CONFLICT (ingredient_id, restock_date) "
                + "DO UPDATE SET quantity = inventory.quantity + EXCLUDED.quantity;";
            restockStatement.executeUpdate(restockQuery);

            rowsCountSet = restockStatement.executeQuery(rowsCount);
            rowsCountSet.next();
            return rowsCountSet.getInt("items") > oldRowsCount;
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    public void restockUtensils(String itemName, int quantity) {
        try {
            Statement restockStatement = this.createStatement();

            String restockQuery = "UPDATE utensils "
                + "SET quantity = current_amount + " + quantity + " "
                + "WHERE name = '" + itemName + "';";
            restockStatement.executeUpdate(restockQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    /**
     * Sets the quantity at or below which the named ingredient should be
     * restocked. If no ingredient has that name, nothing changes.
     *
     * @param itemName the name of the ingredient to update
     * @param threshold the new restock threshold
     */
    public void setIngredientRestockThreshold(String itemName, int threshold) {
        try {
            Statement thresholdStatement = this.createStatement();
            String thresholdQuery = "UPDATE ingredients "
                + "SET restock_threshold = " + threshold + " "
                + "WHERE name = '" + itemName + "';";
            thresholdStatement.executeUpdate(thresholdQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    public void setUtensilRestockThreshold(String itemName, int threshold) {
        try {
            Statement thresholdStatement = this.createStatement();
            String thresholdQuery = "UPDATE utensils "
                + "SET restock_threshold = " + threshold + " "
                + "WHERE name = '" + itemName + "';";
            thresholdStatement.executeUpdate(thresholdQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }
    }

    /**
     * Removes expired stock, ending the current day. Stock is expired once it has
     * been held longer than the ingredient's shelf life, and the removal is
     * permanent.
     *
     * @return the number of inventory records removed
     */
    public int endDay() {
        int deletionCount = 0;
        try {
            Statement deleteStatement = this.createStatement();
            String deleteQuery = "DELETE FROM inventory "
                + "USING ingredients " +
                + "WHERE inventory.ingredient_id = ingredients.id "
                + "AND (inventory.restock_date + ingredients.shelf_life) < CURRENT_DATE;";
            deletionCount = deleteStatement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return deletionCount;
    }
    
    /**
     * Builds a restock report for one storage location. The report has one entry
     * for each ingredient in that location whose stock is at or below its restock
     * threshold, and each entry includes the amount of expired stock on hand.
     *
     * @param location the inventory location to report on
     * @return restock data for the ingredients in that location that need
     *         restocking
     */
    public List<IngredientRestockData> ingredientRestockReport(InventoryLocation location) {
        List <IngredientRestockData> restockReport = new ArrayList<>();

        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "SELECT ing.name, ing.unit, ing.shelf_life, "
                + "ing.restock_threshold, "
                + "SUM(CASE WHEN inv.restock_date + ing.shelf_life < CURRENT_DATE "
                + "THEN inv.quantity ELSE 0 END) AS expired_quantity "
                + "FROM ingredients AS ing "
                + "LEFT JOIN inventory AS inv ON ing.id = inv.ingredient_id "
                + "WHERE ing.inventory_type = '" + location.toString().toLowerCase() + "' "
                + "GROUP BY ing.id "
                + "HAVING COALESCE(SUM(inv.quantity), 0) <= ing.restock_threshold;";
            ResultSet restockResultSet = restockStatement.executeQuery(restockQuery);

            while (restockResultSet.next()) {
                String name = restockResultSet.getString("name");
                Unit unit = this.parseUnit(restockResultSet.getString("unit"));
                int shelfLife = restockResultSet.getInt("shelf_life");
                int restockThreshold = restockResultSet.getInt("restock_threshold");
                int expiredQuantity = restockResultSet.getInt("expired_quantity");

                Ingredient ingredient = new Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .build();

                IngredientRestockData restockData = new IngredientRestockData(ingredient, 
                        expiredQuantity);
                restockReport.add(restockData);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return restockReport;
    }

    public List<Utensil> utensilRestockReport() {
        List<Utensil> restockReport = new ArrayList<>();

        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "SELECT name, quantity, restock_threshold from utensils "
                + "WHERE quantity <= restock_threshold;";

            ResultSet restockResultSet = restockStatement.executeQuery(restockQuery);

            while (restockResultSet.next()) {
                String name = restockResultSet.getString("name");
                int quantity = restockResultSet.getInt("quantity");
                int restockThreshold = restockResultset.getString("restock_threshold");

                Utensil utensil = new Utensil.Builder(name)
                        .quantity(quantity)
                        .restockThreshold(restockThreshold)
                        .build();

                restockReport.add(utensil);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return restockReport;
    }
}
