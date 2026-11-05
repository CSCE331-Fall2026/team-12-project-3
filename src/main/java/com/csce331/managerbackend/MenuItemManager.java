package com.csce331.managerbackend;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.DatabaseSetup;
import com.csce331.common.ExceptionLogger;

/**
 * Reads and updates the restaurant's menu. Queries return only active items;
 * discontinuing an item keeps it in the database so past orders still refer to
 * it.
 *
 * <p>A failed query is logged and ends the program.
 *
 * @author Kedar Panchal
 */
public class MenuItemManager extends DatabaseConnector {
    /**
     * Creates a manager connected to the menu database.
     */
    public MenuItemManager() {
        super();
    }
   
    private MenuItemSize parseSize(String sizeString) {
        switch (sizeString.toLowerCase()) {
            case "small" -> {
                return MenuItemSize.SMALL;
            }
            case "medium" -> {
                return MenuItemSize.MEDIUM;
            }
            case "large" -> {
                return MenuItemSize.LARGE;
            }
            default -> {
                throw new IllegalArgumentException("Invalid menu item size: " + sizeString);
            }
        }
    }
    
    private MenuItemCategory parseCategory(String categoryString) {
        switch (categoryString.toLowerCase()) {
            case "side" -> {
                return MenuItemCategory.SIDE;
            }
            case "entree" -> {
                return MenuItemCategory.ENTREE;
            }
            case "drink" -> {
                return MenuItemCategory.DRINK;
            }
            case "appetizer" -> {
                return MenuItemCategory.APPETIZER;
            }
            default -> {
                throw new IllegalArgumentException("Invalid menu item category: " + categoryString);
            }
        }
    }
    
    /**
     * Returns every active menu item customers can order. Discontinued items are
     * left out.
     *
     * @return all active menu items
     */
    public List<MenuItem> getAllMenuItems() {
        ArrayList<MenuItem> menuItemsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM menu_items WHERE is_active = TRUE";
            Statement statement = this.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                MenuItemSize size = this.parseSize(resultSet.getString("item_size"));
                MenuItemCategory category = this.parseCategory(resultSet.getString("item_category"));
                double price = resultSet.getDouble("price");

                MenuItem item = new MenuItem.Builder(name, category)
                    .size(size)
                    .price(price)
                    .build();
                menuItemsList.add(item);
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        return menuItemsList;
    }
    
    /**
     * Returns the active menu items in one category.
     *
     * @param category the category to filter by
     * @return the menu items in that category
     */
    public List<MenuItem> getMenuItems(MenuItemCategory category) {
        ArrayList<MenuItem> menuItemsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM menu_items "
                + "WHERE item_category = '" + category.toString().toLowerCase()
                + "' AND is_active = TRUE";
            Statement statement = this.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                MenuItemSize size = this.parseSize(resultSet.getString("item_size"));
                double price = resultSet.getDouble("price");

                MenuItem item = new MenuItem.Builder(name, category)
                    .size(size)
                    .price(price)
                    .build();
                menuItemsList.add(item);
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        return menuItemsList;
    }
    
    /**
     * Updates the price of a menu item in the database. An item matches on name
     * and size, so both must match a stored item for the price to change.
     *
     * @param item the menu item to update
     * @param newPrice the new price
     */
    public void updatePrice(MenuItem item, double newPrice) {
        try {
            String updateQuery = "UPDATE menu_items "
                + "SET price = " + newPrice + " "
                + "WHERE name = '" + item.getName() + "' "
                + "AND item_size = '" + item.getSize().toString().toLowerCase() + "'";
            Statement updateStatement = this.createStatement();
            updateStatement.executeUpdate(updateQuery);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
    
    /**
     * Discontinues a menu item by marking it inactive, so it no longer appears in
     * menu queries. The item is matched on name and size, and it stays in the
     * database so past orders are unaffected.
     *
     * @param item the menu item to discontinue
     */
    public void discontinueMenuItem(MenuItem item) {
        try {
            String discontinueQuery = "UPDATE menu_items SET is_active = FALSE " +
                "WHERE name = '" + item.getName() + "'" +
                "AND item_size = '" + item.getSize().toString().toLowerCase() + "'";
            Statement discontinueStatement = this.createStatement();
            discontinueStatement.executeUpdate(discontinueQuery);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
}
