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
 * Manages the restaurant's inventory, providing access to the ingredients and
 * utensils it holds, organized by the location each ingredient is kept in.
 * Supports restocking ingredients, changing their restock thresholds, reporting
 * which ingredients need restocking, discarding expired ingredients at the end
 * of the day, and reporting sales revenue per menu item.
 *
 * @author Kedar Panchal
 */
public class InventoryManager extends DatabaseConnector {

    /**
     * Creates an inventory manager with a connection to the inventory database.
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
                "WHERE inventory_type = " + location.toString().toLowerCase() + ";";
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
     * Adds the given amount to the quantity on hand of the named ingredient.
     * Has no effect if no ingredient has that name. Utensils are not affected
     * by this method.
     *
     * @param itemName the name of the ingredient to restock
     * @param quantity the amount to add to the ingredient's quantity on hand
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
     * Sets the restock threshold of the named ingredient, the quantity on hand at
     * or below which it is considered to need restocking. Has no effect if no
     * ingredient has that name. Utensils are not affected by this method.
     *
     * @param itemName the name of the ingredient to update
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
     * Ends the current day by discarding all expired inventory. Inventory is
     * expired once it has been held for longer than the ingredient's shelf life.
     *
     * @return the number of inventory entries that were removed
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
     * Returns a restock report for the given location, with one entry for each
     * ingredient stored there whose quantity on hand is at or below its restock
     * threshold. Each entry reports the ingredient, its total quantity on hand,
     * and how much of that quantity expires at the end of the day.
     *
     * @param location the inventory location to report on
     * @return the restock data for each ingredient in the location that needs to
     *         be restocked
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
                + "GROUP BY in.id;";
            ResultSet restockResultSet = restockStatement.executeQuery(restockQuery);

            while (restockResultSet.next()) {
                String name = restockResultSet.getString("name");
                Unit unit = this.parseUnit(restockResultSet.getString("unit"));
                int shelfLife = restockResultSet.getInt("shelf_life");
                int restockThreshold = restockResultSet.getInt("restock_threshold");
                int totalQuantity = restockResultSet.getInt("total_quantity");
                int nextExpirationQuantity = restockResultSet.getInt("next_expiration_quantity");

                Ingredient ingredient = new Ingredient.Builder(name, unit, location)
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
     * @param startDate the start date and time of the report range
     * @param endDate the end date and time of the report range
     * @return a map of sale dates to maps of menu item names to their total sales
     *         revenue on that date; dates with no sales are absent
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
