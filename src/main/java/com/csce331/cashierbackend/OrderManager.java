package com.csce331.cashierbackend;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.csce331.common.DatabaseConnector;
import com.csce331.common.ExceptionLogger;

/**
 * Manages cashier operations including querying menu items and executing
 * transactional database writes for customer orders.
 *
 * <p>The manager keeps no order state of its own. Every method performs a
 * database query and returns the resulting data, so the current order is held
 * by the caller and passed in when the order is submitted.
 */
public class OrderManager extends DatabaseConnector {

    /**
     * Creates an order manager with a connection to the cashier database.
     */
    public OrderManager() {
        super();
    }

    /**
     * Builds a cashier menu item from the current row of the given result set.
     *
     * @param menuItemResultSet the result set containing the menu item data
     * @return the menu item represented by the current row
     * @throws SQLException if an SQL error occurs while accessing the result set
     */
    private static MenuItem mapRowToMenuItem(ResultSet menuItemResultSet) throws SQLException {
        int id = menuItemResultSet.getInt("id");
        String name = menuItemResultSet.getString("name");
        String category = menuItemResultSet.getString("item_category");
        String size = menuItemResultSet.getString("item_size");
        double price = menuItemResultSet.getDouble("price");

        byte[] imageBytes = menuItemResultSet.getBytes("image");
        String imageURL = (imageBytes != null && imageBytes.length > 0)
                ? "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes)
                : null;

        return new MenuItem(id, name, category, size, price, imageURL, true);
    }

    /**
     * Retrieves up to 10 popular menu items from the database.
     *
     * @return a map of popular {@link MenuItem} instances mapped to their availability state
     */
    public Map<MenuItem, Boolean> getPopularItems() {
        Map<MenuItem, Boolean> popularItems = new HashMap<>();

        String query =
                "SELECT id, name, item_category, item_size, price, image FROM menu_items "
                + "WHERE is_active = TRUE LIMIT 10";

        try (Statement itemStatement = this.createStatement();
                ResultSet itemResultSet = itemStatement.executeQuery(query)) {
            while (itemResultSet.next()) {
                popularItems.put(mapRowToMenuItem(itemResultSet), true);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
        }

        return popularItems;
    }

    /**
     * Retrieves menu items filtering by a specific category.
     *
     * @param category the category string to query (e.g., "entree", "side", "drink", "appetizer")
     * @return a map of matching {@link MenuItem} instances mapped to their availability state
     */
    public Map<MenuItem, Boolean> getCategoryItems(String category) {
        Map<MenuItem, Boolean> categoryItems = new HashMap<>();

        String query =
                "SELECT id, name, item_category, item_size, price, image FROM menu_items "
                + "WHERE item_category = ?::menu_category AND is_active = TRUE";

        try (PreparedStatement itemStatement = this.getConnection().prepareStatement(query)) {
            itemStatement.setString(1, category.toLowerCase());

            try (ResultSet itemResultSet = itemStatement.executeQuery()) {
                while (itemResultSet.next()) {
                    categoryItems.put(mapRowToMenuItem(itemResultSet), true);
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
        }

        return categoryItems;
    }

    /**
     * A completed order loaded from the database (read-only snapshot).
     *
     * <p>Example usage:
     * <pre>{@code
     * OrderManager om = new OrderManager();
     * for (OrderManager.PlacedOrder o : om.getOrders(LocalDate.of(2026, 10, 16), 14)) {
     *     System.out.println(o.getName() + " " + o.getDate() + " " + o.getTime());
     *     for (MenuItem m : o.getItems()) {
     *         System.out.println("  " + m.getName() + " $" + m.getPrice());
     *     }
     * }
     * }</pre>
     */
    public static class PlacedOrder {
        /** The database identifier of the order. */
        private final int id;
        /** The date the order was placed. */
        private final LocalDate date;
        /** The time the order was placed. */
        private final LocalTime time;
        /** The total price of the order. */
        private final double total;
        /** The items in this order and their quantities. */
        private final Map<MenuItem, Integer> itemQuantities = new LinkedHashMap<>();

        /**
         * Creates a placed order with the given identity and totals.
         *
         * @param id the database identifier of the order
         * @param date the date the order was placed
         * @param time the time the order was placed
         * @param total the total price of the order
         */
        public PlacedOrder(int id, LocalDate date, LocalTime time, double total) {
            this.id = id;
            this.date = date;
            this.time = time;
            this.total = total;
        }

        /**
         * Adds an item to this order, merging quantities for duplicate items.
         *
         * @param item the item to add
         * @param quantity the quantity of the item
         */
        private void addItem(MenuItem item, int quantity) {
            itemQuantities.merge(item, quantity, Integer::sum);
        }

        /**
         * Returns the database identifier of the order.
         *
         * @return the order identifier
         */
        public int getId() { return id; }

        /**
         * Returns a display name for the order.
         *
         * @return the order name
         */
        public String getName() { return "Order #" + id; }

        /**
         * Returns the date the order was placed.
         *
         * @return the order date
         */
        public LocalDate getDate() { return date; }

        /**
         * Returns the time the order was placed.
         *
         * @return the order time
         */
        public LocalTime getTime() { return time; }

        /**
         * Returns the total price of the order.
         *
         * @return the order total
         */
        public double getTotal() { return total; }

        /**
         * Returns the menu items in this order. Each item carries its own price.
         *
         * @return the items in this order
         */
        public List<MenuItem> getItems() { return new ArrayList<>(itemQuantities.keySet()); }

        /**
         * Returns the items in this order and their quantities.
         *
         * @return an unmodifiable view of the order's item quantities
         */
        public Map<MenuItem, Integer> getItemQuantities() {
            return Collections.unmodifiableMap(itemQuantities);
        }
    }

    /**
     * Retrieves every order placed on the given day.
     *
     * @param day the date to look up
     * @return the day's orders sorted by time (oldest first), or an empty list if there are
     *         none or a database error occurs (errors are logged)
     */
    public List<PlacedOrder> getOrders(LocalDate day) {
        return this.queryOrders(day, null);
    }

    /**
     * Retrieves every order placed during a specific hour of the given day.
     *
     * @param day  the date to look up
     * @param hour the hour of day on a 24-hour clock, 0-23 (e.g. 14 for 2:00-2:59 PM)
     * @return the matching orders sorted by time (oldest first), or an empty list if there
     *         are none or a database error occurs (errors are logged)
     * @throws IllegalArgumentException if {@code hour} is not between 0 and 23
     */
    public List<PlacedOrder> getOrders(LocalDate day, int hour) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("hour must be between 0 and 23");
        }
        return this.queryOrders(day, hour);
    }

    /**
     * Shared query behind both {@code getOrders} overloads. Joins {@code orders},
     * {@code order_menu}, and {@code menu_items}, then groups the rows into one
     * {@link PlacedOrder} per order id.
     *
     * @param day  the date to filter on
     * @param hour the hour (0-23) to filter on, or {@code null} for the whole day
     * @return the matching orders sorted by time, or an empty list on error
     */
    private List<PlacedOrder> queryOrders(LocalDate day, Integer hour) {
        String query =
                "SELECT o.id AS order_id, o.order_date, o.order_time, o.price AS order_price, "
                + "       m.id, m.name, m.item_category, m.item_size, m.price, m.image, "
                + "       om.quantity "
                + "FROM orders o "
                + "JOIN order_menu om ON om.order_id = o.id "
                + "JOIN menu_items m ON m.id = om.menu_item_id "
                + "WHERE o.order_date = ? "
                + (hour != null ? "AND EXTRACT(HOUR FROM o.order_time) = ? " : "")
                + "ORDER BY o.order_time, o.id";

        Map<Integer, PlacedOrder> orders = new LinkedHashMap<>();

        try (PreparedStatement orderStatement = this.getConnection().prepareStatement(query)) {
            orderStatement.setDate(1, Date.valueOf(day));
            if (hour != null) {
                orderStatement.setInt(2, hour);
            }

            try (ResultSet orderResultSet = orderStatement.executeQuery()) {
                while (orderResultSet.next()) {
                    int orderId = orderResultSet.getInt("order_id");
                    PlacedOrder order = orders.get(orderId);
                    if (order == null) {
                        order = new PlacedOrder(
                                orderId,
                                orderResultSet.getDate("order_date").toLocalDate(),
                                orderResultSet.getTime("order_time").toLocalTime(),
                                orderResultSet.getDouble("order_price"));
                        orders.put(orderId, order);
                    }
                    order.addItem(mapRowToMenuItem(orderResultSet), orderResultSet.getInt("quantity"));
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e);
        }

        return new ArrayList<>(orders.values());
    }

    /**
     * Finalizes and submits an order to PostgreSQL in an atomic transaction.
     * Inserts the record into the {@code orders} table and the corresponding items into
     * {@code order_menu}, then removes the ingredients used from the {@code inventory} table.
     * Inventory batches that expire soonest (but have not expired yet) are consumed first.
     *
     * @param items      the ordered menu items and their quantities
     * @param method     the {@link PaymentMethod} enum used for checkout
     * @param totalPrice the grand total calculation for the order
     * @return the generated database identifier of the placed order, or -1 if the order is
     *         empty or the transaction fails (errors are logged and rolled back)
     */
    public int order(Map<MenuItem, Integer> items, PaymentMethod method, double totalPrice) {
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
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                ExceptionLogger.logException(e);
            }
        }
    }

    /**
     * Removes the ingredient quantities used by an order from the inventory batches
     * that expire soonest. Expired batches are ignored, so the transaction never
     * consumes inventory that should have been discarded at the end of the day.
     *
     * <p>Each batch is consumed completely before moving on to the next one. If the
     * order needs more of an ingredient than the unexpired batches can supply, the
     * available amount is removed and the rest is left unrecorded.
     *
     * @param conn    the transaction connection
     * @param orderId the order whose ingredients should be removed
     * @return the number of inventory batches that were updated
     * @throws SQLException if a query or update fails
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
