package com.csce331.managers;

import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.ExceptionLogger;

import com.csce331.models.Ingredient;
import com.csce331.models.IngredientRestockData;
import com.csce331.models.InventoryLocation;
import com.csce331.models.Unit;
import com.csce331.models.Utensil;

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

    /**
     * Converts an inventory location name stored in the database into the
     * matching inventory location.
     *
     * @param inventoryType the location name, case-insensitive
     * @return the matching inventory location
     * @throws IllegalArgumentException if the name is not freezer,
     *         refrigerator, or pantry
     */
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

    /**
     * Converts a unit name stored in the database into the matching unit.
     *
     * @param unit the unit name, case-insensitive
     * @return the matching unit
     * @throws IllegalArgumentException if the name is not kilograms, liters, or
     *         items
     */
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
     * @throws SQLException if the database query fails
     */
    public List<Ingredient> getIngredients() throws SQLException {
        ArrayList<Ingredient> allIngredients = new ArrayList<>();

        try {
            Statement ingredientStatement = this.createStatement();
            String ingredientQuery = "SELECT i.name, i.inventory_type, i.unit, i.shelf_life, "
                + "i.restock_threshold, COALESCE(SUM(inv.quantity), 0) AS quantity "
                + "FROM ingredients AS i "
                + "LEFT JOIN inventory AS inv ON inv.ingredient_id = i.id "
                + "GROUP BY i.id, i.name, i.inventory_type, i.unit, i.shelf_life, i.restock_threshold;";
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
                    .quantity(ingredientResultSet.getBigDecimal("quantity"))
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
     * @throws SQLException if the database query fails
     * @throws IllegalArgumentException if the location is {@code UTENSILS}
     */
    public List<Ingredient> getIngredients(InventoryLocation location) throws SQLException {
        if (location == InventoryLocation.UTENSILS) {
            throw new IllegalArgumentException("Cannot get ingredients from utensils");
        }

        ArrayList<Ingredient> ingredientsInLocation = new ArrayList<>();

        try {
            String ingredientQuery = "SELECT i.name, i.unit, i.shelf_life, i.restock_threshold, "
                + "COALESCE(SUM(inv.quantity), 0) AS quantity "
                + "FROM ingredients AS i "
                + "LEFT JOIN inventory AS inv ON inv.ingredient_id = i.id "
                + "WHERE i.inventory_type = ?::inventory_location "
                + "GROUP BY i.id, i.name, i.unit, i.shelf_life, i.restock_threshold;";
            PreparedStatement ingredientStatement = this.getConnection().prepareStatement(ingredientQuery);
            ingredientStatement.setString(1, location.name().toLowerCase());
            ResultSet ingredientResultSet = ingredientStatement.executeQuery();

            while (ingredientResultSet.next()) {
                String name = ingredientResultSet.getString("name");
                Unit unit = this.parseUnit(ingredientResultSet.getString("unit"));
                int shelfLife = ingredientResultSet.getInt("shelf_life");
                int restockThreshold = ingredientResultSet.getInt("restock_threshold");

                Ingredient ingredient = new Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .quantity(ingredientResultSet.getBigDecimal("quantity"))
                    .build();
                ingredientsInLocation.add(ingredient);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return ingredientsInLocation;
    }
    
    /**
     * Returns every utensil tracked in the inventory.
     *
     * @return a list of all utensils
     * @throws SQLException if the database query fails
     */
    public List<Utensil> getUtensils() throws SQLException {
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
     * @throws SQLException if the database update fails
     */
    public boolean restockIngredients(String itemName, int quantity) throws SQLException {
        try {
            Statement restockStatement = this.createStatement();

            String rowsCount = "SELECT COUNT(*) AS items FROM inventory;";
            ResultSet rowsCountSet = restockStatement.executeQuery(rowsCount);
            rowsCountSet.next();
            int oldRowsCount = rowsCountSet.getInt("items");

            String restockQuery = "INSERT INTO inventory (ingredient_id, quantity, restock_date) "
                + "SELECT id, " + quantity + ", CURRENT_DATE FROM ingredients "
                + "WHERE name = '" + itemName + "' "
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

    /**
     * Adds the given amount to the named utensil's stock. If no utensil has
     * that name, nothing changes.
     *
     * @param itemName the name of the utensil to restock
     * @param quantity the amount to add
     * @throws SQLException if the database update fails
     */
    public void restockUtensils(String itemName, int quantity) throws SQLException {
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
     * @throws SQLException if the database update fails
     */
    public void setIngredientRestockThreshold(String itemName, int threshold) throws SQLException {
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

    /**
     * Sets the quantity at or below which the named utensil should be
     * restocked. If no utensil has that name, nothing changes.
     *
     * @param itemName the name of the utensil to update
     * @param threshold the new restock threshold
     * @throws SQLException if the database update fails
     */
    public void setUtensilRestockThreshold(String itemName, int threshold) throws SQLException {
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
     * @throws SQLException if the database delete fails
     */
    public int endDay() throws SQLException {
        int deletionCount = 0;
        try {
            Statement deleteStatement = this.createStatement();
            String deleteQuery = "DELETE FROM inventory "
                + "USING ingredients "
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
     * @throws SQLException if the database query fails
     * @throws IllegalArgumentException if the location is {@code UTENSILS}
     */
    public List<IngredientRestockData> ingredientRestockReport(InventoryLocation location)
            throws SQLException {
        if (location == InventoryLocation.UTENSILS) {
            throw new IllegalArgumentException("Cannot get restock report for utensils");
        }

        List <IngredientRestockData> restockReport = new ArrayList<>();

        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "SELECT ing.name, ing.unit, ing.shelf_life, "
                + "ing.restock_threshold, COALESCE(SUM(inv.quantity), 0) AS total_quantity, "
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
                    .quantity(restockResultSet.getBigDecimal("total_quantity"))
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

    /**
     * Builds a restock report for the utensils. The report has one entry for
     * each utensil whose stock is at or below its restock threshold.
     *
     * @return restock data for the utensils that need restocking
     * @throws SQLException if the database query fails
     */
    public List<Utensil> utensilRestockReport() throws SQLException {
        List<Utensil> restockReport = new ArrayList<>();

        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "SELECT name, quantity, restock_threshold from utensils "
                + "WHERE quantity <= restock_threshold;";

            ResultSet restockResultSet = restockStatement.executeQuery(restockQuery);
            while (restockResultSet.next()) {
                String name = restockResultSet.getString("name");
                int quantity = restockResultSet.getInt("quantity");
                int restockThreshold = restockResultSet.getInt("restock_threshold");

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
