package com.csce331.managers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.ExceptionLogger;

import com.csce331.models.MenuItem;
import com.csce331.models.MenuItemCategory;
import com.csce331.models.MenuItemSize;
import com.csce331.models.Order;
import com.csce331.models.PaymentMethod;

/**
 * Handles cashier operations: browsing the menu and placing customer orders.
 *
 * <p>The manager keeps no order state of its own. Each method reads from or
 * writes to the database and returns the result, so the current order lives
 * with the caller and is passed in at checkout.
 *
 * <p>A failed query is logged and rethrown.
 */
public class OrderManager extends DatabaseConnector {

    /**
     * Creates a manager connected to the restaurant database.
     */
    public OrderManager() {
        super();
    }

    /**
     * Converts a result set row containing the menu item columns into a menu
     * item.
     *
     * @param menuItemResultSet the result set positioned on the row to read
     * @return the menu item in that row
     * @throws SQLException if a column cannot be read
     */
    private static MenuItem mapRowToMenuItem(ResultSet menuItemResultSet) throws SQLException {
        int id = menuItemResultSet.getInt("id");
        String name = menuItemResultSet.getString("name");
        MenuItemCategory category =
                MenuItemCategory.fromString(menuItemResultSet.getString("item_category"));
        MenuItemSize size = MenuItemSize.fromString(menuItemResultSet.getString("item_size"));
        double price = menuItemResultSet.getDouble("price");

        byte[] imageBytes = menuItemResultSet.getBytes("image");
        String imageURL = (imageBytes != null && imageBytes.length > 0)
                ? "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes)
                : null;

        return new MenuItem(id, name, category, size, price, imageURL, true);
    }

    /**
     * Returns the most popular active menu items, most ordered first. An item's
     * popularity is the total quantity ordered across all placed orders, and
     * only items that have been ordered are included.
     *
     * @return up to 10 active menu items, ordered by total quantity ordered
     * @throws SQLException if the query fails
     */
    public List<MenuItem> getPopularItems() throws SQLException {
        List<MenuItem> popularItems = new ArrayList<>();

        String query =
                "SELECT m.id, m.name, m.item_category, m.item_size, m.price, m.image "
                + "FROM menu_items m "
                + "JOIN order_menu om ON om.menu_item_id = m.id "
                + "WHERE m.is_active = TRUE "
                + "GROUP BY m.id, m.name, m.item_category, m.item_size, m.price, m.image "
                + "ORDER BY SUM(om.quantity) DESC, m.name, m.item_size "
                + "LIMIT 10";

        try (PreparedStatement itemStatement = this.getConnection().prepareStatement(query);
                ResultSet itemResultSet = itemStatement.executeQuery()) {
            while (itemResultSet.next()) {
                popularItems.add(mapRowToMenuItem(itemResultSet));
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return popularItems;
    }

    /**
     * Returns the active menu items in one category.
     *
     * @param category the category to filter by
     * @return the matching active items
     * @throws SQLException if the query fails
     */
    public List<MenuItem> getCategoryItems(MenuItemCategory category) throws SQLException {
        List<MenuItem> categoryItems = new ArrayList<>();

        String query =
                "SELECT id, name, item_category, item_size, price, image FROM menu_items "
                + "WHERE item_category = ?::menu_category AND is_active = TRUE";

        try (PreparedStatement itemStatement = this.getConnection().prepareStatement(query)) {
            itemStatement.setString(1, category.toString());

            try (ResultSet itemResultSet = itemStatement.executeQuery()) {
                while (itemResultSet.next()) {
                    categoryItems.add(mapRowToMenuItem(itemResultSet));
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
            throw e;
        }

        return categoryItems;
    }

    /**
     * Places an order at the current date and time and saves it in a single
     * transaction. The items and quantities are stored with the order, and the
     * ingredients they use are taken from inventory starting with the stock that
     * expires soonest. Expired stock is not used. If inventory runs short, the
     * order still goes through and the missing amount is not recorded.
     *
     * <p>The total is stored as given; the manager does not recompute it from the
     * item prices.
     *
     * @param items      the ordered menu items and their quantities
     * @param method     the payment method used at checkout
     * @param totalPrice the total to store for the order
     * @return the new order's database identifier, or -1 if the order is empty
     * @throws SQLException if the order cannot be saved; the transaction is
     *         rolled back first
     */
    public int order(Map<MenuItem, Integer> items, PaymentMethod method, double totalPrice)
            throws SQLException {
        if (items == null || items.isEmpty()) {
            return -1; // Don't process empty orders
        }

        String insertOrderSQL =
                "INSERT INTO orders (order_time, order_date, payment_method, price) "
                + "VALUES (?, ?, ?::payment_type, ?) RETURNING id";
        String insertOrderMenuSQL =
                "INSERT INTO order_menu (order_id, menu_item_id, quantity) VALUES (?, ?, ?)";

        String dbPaymentType = method.name().toLowerCase().replace("_", " ");
        Connection conn = this.getConnection();

        try {
            conn.setAutoCommit(false);

            int generatedOrderId = -1;
            try (PreparedStatement orderStatement = conn.prepareStatement(insertOrderSQL)) {
                orderStatement.setTime(1, Time.valueOf(LocalTime.now()));
                orderStatement.setDate(2, Date.valueOf(LocalDate.now()));
                orderStatement.setString(3, dbPaymentType);
                orderStatement.setDouble(4, totalPrice);

                try (ResultSet orderResultSet = orderStatement.executeQuery()) {
                    if (orderResultSet.next()) {
                        generatedOrderId = orderResultSet.getInt(1);
                    }
                }
            }

            if (generatedOrderId == -1) {
                throw new SQLException("Failed to retrieve generated order ID.");
            }

            try (PreparedStatement lineItemStatement =
                    conn.prepareStatement(insertOrderMenuSQL)) {
                for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
                    lineItemStatement.setInt(1, generatedOrderId);
                    lineItemStatement.setInt(2, entry.getKey().getId());
                    lineItemStatement.setInt(3, entry.getValue());
                    lineItemStatement.addBatch();
                }
                lineItemStatement.executeBatch();
            }

            // Remove the ingredients used by this order from inventory
            this.removeInventoryForOrder(conn, generatedOrderId);

            conn.commit();
            return generatedOrderId;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackException) {
                ExceptionLogger.logException(rollbackException);
            }
            ExceptionLogger.logException(e);
            throw e;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                ExceptionLogger.logException(e);
            }
        }
    }

    /**
     * Removes the ingredients used by an order from inventory, taking each
     * ingredient from the stock that expires soonest and never using expired
     * stock. If inventory runs short, the missing amount is not recorded.
     *
     * @param conn the connection holding the order's transaction
     * @param orderId the order whose ingredients should be removed
     * @return the number of inventory batches updated
     * @throws SQLException if the inventory cannot be read or updated
     */
    private int removeInventoryForOrder(Connection conn, int orderId) throws SQLException {
        String usageQuery =
                "SELECT mi.ingredient_id, SUM(mi.quantity * om.quantity) AS used "
                + "FROM order_menu om "
                + "JOIN menu_ingredients mi ON mi.menu_item_id = om.menu_item_id "
                + "WHERE om.order_id = ? "
                + "GROUP BY mi.ingredient_id";

        String batchQuery =
                "SELECT inventory.id, inventory.quantity "
                + "FROM inventory "
                + "JOIN ingredients ON ingredients.id = inventory.ingredient_id "
                + "WHERE inventory.ingredient_id = ? "
                + "AND inventory.quantity > 0 "
                + "AND inventory.restock_date + ingredients.shelf_life >= CURRENT_DATE "
                + "ORDER BY inventory.restock_date + ingredients.shelf_life, "
                + "inventory.restock_date, inventory.id "
                + "FOR UPDATE OF inventory";

        String updateQuery = "UPDATE inventory SET quantity = ? WHERE id = ?";

        // Find how much of each ingredient the order uses.
        Map<Integer, BigDecimal> usedByIngredient = new HashMap<>();
        try (PreparedStatement usageStatement = conn.prepareStatement(usageQuery)) {
            usageStatement.setInt(1, orderId);
            try (ResultSet usageResultSet = usageStatement.executeQuery()) {
                while (usageResultSet.next()) {
                    usedByIngredient.put(
                            usageResultSet.getInt("ingredient_id"),
                            usageResultSet.getBigDecimal("used"));
                }
            }
        }

        int updatedBatchCount = 0;

        // Consume each ingredient from its soonest-expiring batches first.
        try (PreparedStatement batchStatement = conn.prepareStatement(batchQuery);
                PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
            for (Map.Entry<Integer, BigDecimal> entry : usedByIngredient.entrySet()) {
                BigDecimal remaining = entry.getValue();
                batchStatement.setInt(1, entry.getKey());

                try (ResultSet batchResultSet = batchStatement.executeQuery()) {
                    while (remaining.signum() > 0 && batchResultSet.next()) {
                        BigDecimal available = batchResultSet.getBigDecimal("quantity");
                        BigDecimal used = available.min(remaining);
                        updateStatement.setBigDecimal(1, available.subtract(used));
                        updateStatement.setInt(2, batchResultSet.getInt("id"));
                        updateStatement.addBatch();
                        remaining = remaining.subtract(used);
                        updatedBatchCount++;
                    }
                }
            }
            updateStatement.executeBatch();
        }

        return updatedBatchCount;
    }
}
