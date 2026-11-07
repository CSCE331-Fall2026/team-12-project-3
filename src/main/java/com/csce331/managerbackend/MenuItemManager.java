package com.csce331.managerbackend;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.DatabaseSetup;
import com.csce331.common.ExceptionLogger;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.models.MenuItemSize;

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

    /**
     * Returns every active menu item customers can order. Discontinued items are
     * left out.
     *
     * @return all active menu items
     */
    public List<MenuItem> getMenuItems() throws SQLException {
        ArrayList<MenuItem> menuItemsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM menu_items WHERE is_active = TRUE";
            Statement statement = this.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                MenuItemSize size = MenuItemSize.fromString(resultSet.getString("item_size"));
                MenuItemCategory category =
                    MenuItemCategory.fromString(resultSet.getString("item_category"));
                double price = resultSet.getDouble("price");

                MenuItem item = new MenuItem.Builder(name, category)
                    .size(size)
                    .price(price)
                    .build();
                menuItemsList.add(item);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return menuItemsList;
    }
    
    /**
     * Returns the active menu items in one category.
     *
     * @param category the category to filter by
     * @return the menu items in that category
     */
    public List<MenuItem> getMenuItems(MenuItemCategory category) throws SQLException {
        ArrayList<MenuItem> menuItemsList = new ArrayList<>();

        try {
            String query = "SELECT * FROM menu_items "
                + "WHERE item_category = '" + category.toString().toLowerCase()
                + "' AND is_active = TRUE";
            Statement statement = this.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                MenuItemSize size = MenuItemSize.fromString(resultSet.getString("item_size"));
                double price = resultSet.getDouble("price");

                MenuItem item = new MenuItem.Builder(name, category)
                    .size(size)
                    .price(price)
                    .build();
                menuItemsList.add(item);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
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
    public void updatePrice(String name, MenuItemSize size, double newPrice) {
        try {
            String updateQuery = "UPDATE menu_items "
                + "SET price = " + newPrice + " "
                + "WHERE name = '" + name + "' "
                + "AND item_size = '" + size.toString().toLowerCase() + "'";
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
    public void discontinueMenuItem(String name, MenuItemSize size) {
        try {
            String discontinueQuery = "UPDATE menu_items SET is_active = FALSE " +
                "WHERE name = '" + name + "'" +
                "AND item_size = '" + size.toString().toLowerCase() + "'";
            Statement discontinueStatement = this.createStatement();
            discontinueStatement.executeUpdate(discontinueQuery);
        } catch (Exception e) {
            ExceptionLogger.logException(e);
            System.exit(0);
        }
    }

    /**
     * Returns the revenue each menu item earned, grouped by sale date. An order
     * counts only if both its date and its time fall within the range.
     *
     * @param startDate the first date and time in the range
     * @param endDate the last date and time in the range
     * @return a map of sale dates to menu item names and their revenue on that
     *         date; dates with no sales are absent
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
