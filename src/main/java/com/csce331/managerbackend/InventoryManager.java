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

import com.csce331.common.DatabaseSetup;
import com.csce331.common.ExceptionLogger;

/**
 * Manages the restaurant's inventory, loading ingredients and utensils from the
 * database and providing access to them by location. Supports restocking
 * inventory items, adjusting their restock thresholds, and end-of-day
 * expiration handling.
 *
 * @author Kedar Panchal
 */
public class InventoryManager extends DatabaseConnector {

    /**
     * Creates an inventory manager, opening a database connection and loading
     * all ingredients and utensils and their current inventory into memory.
     */
    public InventoryManager() {
        super();
    }

    /**
     * Parses an inventory location from its database string representation.
     *
     * @param inventoryType the raw inventory type value
     * @return the matching inventory location
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
     * Parses a unit from its database string representation.
     *
     * @param unit the raw unit value
     * @return the matching unit
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
     */
    public List<Ingredient> getAllIngredients() {
        ArrayList<Ingredient> allIngredients = new ArrayList<>();

        try {
            Statement ingredientStatement = this.createStatement();
            String ingredientQuery = "SELECT * FROM ingredients;";
            ResultSet ingredientResultSet = ingredientStatement.executeQuert(ingredientQuery);

            while (ingredientResultSet.next()) {
                String name = ingredientResultSet.getString("name");
                InventoryLocation location = this.parseLocation(ingredientResultSet.getString("inventory_type"));
                Unit unit = this.parseUnit(ingredientResultSet.getString("unit"));
                int shelfLife = ingredientResultSet.getInt("shelf_life");
                int restockThreshold = ingredientResultSet.getInt("restock_threshold");

                Ingredient ingredient = Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .build();
                allIngredients.add(ingredient);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            System.exit(0);
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
            String ingredientQuery = "SELECT * FROM ingredients " +
                "WHERE inventory_type = " location.toString().toLowerCase() + ";";
            ResultSet ingredientResultSet = ingredientStatement.executeQuery(ingredientQuery);

            while (ingredientResultSet.next()) {
                String name = ingredientResultSet.getString("name");
                Unit unit = this.parseUnit(ingredientResultSet.getString("unit"));
                int shelfLife = ingredientResultSet.getInt("shelf_life");
                int restockThreshold = ingredientResultSet.getInt("restock_threshold");

                Ingredient ingredient = Ingredient.Builder(name, unit, location)
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
     * Returns every utensil in the inventory.
     *
     * @return a list of all utensils
     */
    public List<Utensil> getUtensils() {
        ArrayList<Ingredient> allUtensils = new ArrayList<>();

        try {
            Statement utensilStatement = this.createStatement();
            String utensilQuery = "SELECT * FROM utensils;";
            ResultSet utensilResultSet = utensilStatement.executeQuery(utensilQuery);

            while (utensilResultSet.next()) {
                String name = utensilResultSet.getString("name");
                int restockThreshold = utensilResultSet.getInt("restock_threshold");

                Utensil utensil = new Utensil(name)
                    .restockThreshold(restockThreshold)
                    .quantity(utensilResultSet.getInt("current_amount"))
                    .build();
                allUtensils.add(utensil);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
    
    /**
     * Restocks the given inventory item by the specified quantity, updating both
     * the database and in-memory inventory. Has no effect if the item is not
     * part of the inventory.
     *
     * @param item the inventory item to restock
     * @param quantity the amount to add
     */
    public void restock(String itemName, int quantity) {
        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "UPDATE inventory " +
                "SET current_amount = current_amount + " + quantity + " " +
                "WHERE ingredient_id IN (SELECT id FROM ingredients WHERE name = '" + itemName + "');";
            restockStatement.executeUpdate(restockQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }

    /**
     * Sets the restock threshold of the given inventory item, updating both the
     * database and in-memory inventory. Has no effect if the item is not part of
     * the inventory.
     *
     * @param item the inventory item to update
     * @param threshold the new restock threshold
     */
    public void setRestockThreshold(String itemName, int threshold) {
        try {
            Statement thresholdStatement = this.createStatement();
            String thresholdQuery = "UPDATE ingredients " +
                "SET restock_threshold = " + threshold + " " +
                "WHERE name = '" + itemName + "';";
            thresholdStatement.executeUpdate(thresholdQuery);
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }

    /**
     * Ends the current day by removing all expired inventory from both the
     * database and in-memory inventory.
     *
     * @return the list of inventory items that were removed due to expiration
     */
    public int endDay() {
        int deletionCount = 0;
        try {
            Statement deleteStatement = this.createStatement();
            String deleteQuery = "DELETE FROM inventory " +
                "USING ingredients " +
                "WHERE inventory.ingredient_id = ingredients.id " +
                "AND (inventory.restock_date + ingredients.shelf_life) < CURRENT_DATE;";
            deletionCount = deleteStatement.executeUpdate(deleteQuery);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        return deletionCount;
    }
    
    /**
     * Returns a report of ingredients that need to be restocked in the given
     * location, along with the amount of each ingredient that will expire at the
     * end of the day.
     *
     * @param location the inventory location to query
     * @return a list of ingredient restock data for ingredients that need to be
     *         restocked in that location
     */
    public List<IngredientRestockData> ingredientRestockReport(InventoryLocation location) {
        List <IngredientRestockData> restockReport = new ArrayList<>();

        try {
            Statement restockStatement = this.createStatement();
            String restockQuery = "SELECT in.*, "
                + "inv.quantity AS next_expiration_quantity "
                + "SUM(inv.quantity) AS total_quantity, "
                + "FROM ingredients AS in "
                + "JOIN inventory AS inv "
                + "ON ned.ingredient_id = inv.ingredient_id "
                + "AND (inv.restock_date + in.shelf_life - CURRENT_DATE) IN ( "
                    + "SELECT in.id AS ingredient_id, "
                    + "MIN(iv.restock_date + in.shelf_life - CURRENT_DATE) AS next_expiration_date "
                    + "FROM ingredients AS in "
                    + "JOIN inventory AS iv "
                    + "ON in.id = iv.ingredient_id "
                    + "GROUP BY in.id, inv.quantity "
                + ") "
                + "WHERE in.inventory_type = '" + location.toString().toLowerCase() + "' "
                + "GROUP BY in.id;"
            ResultSet restockResultSet = restockStatement.executeQuery(restockQuery);

            while (restockResultSet.next()) {
                String name = restockResultSet.getString("name");
                Unit unit = this.parseUnit(restockResultSet.getString("unit"));
                int shelfLife = restockResultSet.getInt("shelf_life");
                int restockThreshold = restockResultSet.getInt("restock_threshold");
                int totalQuantity = restockResultSet.getInt("total_quantity");
                int nextExpirationQuantity = restockResultSet.getInt("next_expiration_quantity");

                Ingredient ingredient = Ingredient.Builder(name, unit, location)
                    .lifespan(shelfLife)
                    .restockThreshold(restockThreshold)
                    .build();

                if (ingredient.isRestockNeeded()) {
                    IngredientRestockData restockData = new IngredientRestockData(
                        ingredient, totalQuantity, nextExpirationQuantity);
                    restockReport.add(restockData);
                }
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        return restockReport;
    }

    /**
     * Returns a report of sales revenue per menu item for the given date range.
     *
     * Sales reports include the total revenue generated by each menu item during the specified
     * date range.
     *
     * @param startDate the start date and time of the report range
     * @param endDate the end date and time of the report range
     * @return a map of dates to maps of menu item names to their total sales revenue during the
     * specified date range
     */
    public Map<LocalDate, Map<String, Double>> menuItemSalesReport(
            LocalDateTime startDate, LocalDateTime endDate) {
        LocalDate sqlStartDate = startDate.toLocalDate();
        LocalDate sqlEndDate = endDate.toLocalDate();
        LocalTime sqlStartTime = startDate.toLocalTime();
        LocalTime sqlEndTime = endDate.toLocalTime();
        Map<LocalDate, Map<String, Double>> result = new HashMap<>();

        try {
            Statement salesStatement = this.createStatement();
            String salesQuery =
                "SELECT mi.name, o.order_date AS sale_date, " +
                "SUM(om.quantity * mi.price) AS total_sales " +
                "FROM order_menu AS om " +
                "JOIN orders AS o " +
                "ON om.order_id = o.id " +
                "JOIN menu_items AS mi " +
                "ON om.menu_item_id = mi.id " +
                "WHERE o.order_date BETWEEN '" + sqlStartDate.toString() + "' " +
                "AND '" + sqlEndDate.toString() + "' " +
                "AND o.order_time BETWEEN '" + sqlStartTime.toString() + "' " +
                "AND '" + sqlEndTime.toString() + "' " +
                "GROUP BY mi.name, o.order_date;";

            ResultSet salesResultSet = salesStatement.executeQuery(salesQuery);
            while (salesResultSet.next()) {
                LocalDate saleDate = salesResultSet.getDate("sale_date").toLocalDate();
                result.putIfAbsent(saleDate, new HashMap<>());
                result.get(saleDate)
                    .put(salesResultSet.getString("name"), salesResultSet.getDouble("total_sales"));
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
        return result;
    }
}
