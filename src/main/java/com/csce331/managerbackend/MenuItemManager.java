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
 * A manager for the menu items in the restaurant, allowing for retrieval and
 * modification of menu items.
 *
 * @author Kedar Panchal
 */
public class MenuItemManager extends DatabaseConnector {
    /**
     * Constructs a new {@code MenuItemManager} and loads all menu items from the database.
     */
    public MenuItemManager() {
        super();
    }
   
    /**
     * Parses a menu item size from its database string representation.
     *
     * @param sizeString the string representation of the menu item size
     * @return the matching menu item size
     */
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
    
    /**
     * Parses a menu item category from its database string representation.
     *
     * @param categoryString the string representation of the menu item category
     * @return the matching menu item category
     */
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
     * Returns a list of all active menu items customers can buy.
     * Discontinued items are excluded.
     *
     * @return a list of all active menu items
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
                MenuItemCategory category = this.parseCategory(resultSet.getString("category"));
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
     * Returns a list of all menu items in the specified category.
     *
     * @param category the category to filter by
     * @return a list of menu items in the specified category
     */
    public List<MenuItem> getMenuItems(MenuItemCategory category) {
        ArrayList<MenuItem> menuItemsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM menu_items "
                + "WHERE category = '" + category.toString().toLowerCase() 
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
     * Updates the price of the specified menu item in the database. If the menu item does not 
     * exist, no action is taken.
     *
     * @param item the menu item to update
     * @param newPrice the new price for the menu item
     */
    public void updatePrice(MenuItem item, double newPrice) {
        try {
            String updateQuery = "UPDATE menu_items "
                + "SET price = " + newPrice + " " +
                "WHERE name = '" + item.getName() + " "
                + "'AND item_size = '" + item.getSize().toString().toLowerCase() + "'";
            Statement updateStatement = this.createStatement();
            updateStatement.executeUpdate(updateQuery);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
    
    /**
     * Discontinues the specified menu item, marking it inactive in the database and removing
     * it from the in-memory inventory. Past orders referencing the item are preserved.
     * If the menu item does not exist, no action is taken.
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
