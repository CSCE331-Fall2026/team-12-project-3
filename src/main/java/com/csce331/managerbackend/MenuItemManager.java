package com.csce331.managerbackend;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

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
    /** The active menu items, keyed by name. */
    private Map<String, MenuItem> menuItems;
    
    /**
     * Constructs a new {@code MenuItemManager} and loads all menu items from the database.
     */
    public MenuItemManager() {
        super();

        this.menuItems = new HashMap<>();

        this.loadMenuItems();
    }
    
    /**
     * Loads all menu items from the database into the in-memory inventory, exiting the application
     * if the query fails.
     */
    private void loadMenuItems() {
        Statement menuItemStatement = null;
        try {
            menuItemStatement = this.createStatement();
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        ResultSet menuItemResultSet = null;
        try {
            menuItemResultSet = menuItemStatement.executeQuery(
                "SELECT name, price, item_size, item_category FROM menu_items "
                    + "WHERE is_active = TRUE;"
            );
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }

        try {
            while (menuItemResultSet.next()) {
                MenuItem item = this.buildMenuItem(menuItemResultSet);
                this.menuItems.put(item.getName(), item);
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
    
    /**
     * Builds a menu item from the current row of the given result set and adds it to the in-memory 
     * inventory.
     *
     * @param menuItemResultSet the result set containing the menu item data
     * @return the constructed menu item
     * @throws SQLException if an SQL error occurs while accessing the result set
     */
    private MenuItem buildMenuItem(ResultSet menuItemResultSet) throws SQLException {
        MenuItemSize size = this.parseSize(menuItemResultSet.getString("item_size"));
        MenuItemCategory category = this.parseCategory(
                menuItemResultSet.getString("item_category"));
        MenuItem item = new MenuItem.Builder(menuItemResultSet.getString("name"), category)
                .price(menuItemResultSet.getDouble("price"))
                .size(size)
                .build();

        return item;
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
        return List.copyOf(this.menuItems.values());
    }
    
    /**
     * Returns a list of all menu items in the specified category.
     *
     * @param category the category to filter by
     * @return a list of menu items in the specified category
     */
    public List<MenuItem> getMenuItems(MenuItemCategory category) {
        return this.menuItems.values().stream()
                .filter(item -> item.getCategory() == category)
                .toList();
    }
    
    /**
     * Updates the price of the specified menu item in the database. If the menu item does not 
     * exist, no action is taken.
     *
     * @param item the menu item to update
     * @param newPrice the new price for the menu item
     */
    public void updatePrice(MenuItem item, double newPrice) {
        if (!this.menuItems.containsKey(item.getName())) {
            return;
        }

        String updateQuery = this.menuItems.get(item.getName()).setPrice(newPrice);
        try {
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
        if (!this.menuItems.containsKey(item.getName())) {
            return;
        }

        String discontinueQuery = "UPDATE menu_items SET is_active = FALSE " +
            "WHERE name = '" + item.getName() + "'" +
            "AND item_size = '" + item.getSize().toString().toLowerCase() + "'";
        try {
            Statement discontinueStatement = this.createStatement();
            discontinueStatement.executeUpdate(discontinueQuery);
            this.menuItems.remove(item.getName());
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }
}
