package com.csce331.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An order read from the database. Its items, date, time, and total are fixed
 * once it is loaded.
 *
 * <p>Example usage:
 * <pre>{@code
 * OrderManager om = new OrderManager();
 * for (Order o : om.getOrders(LocalDate.of(2026, 10, 16), 14)) {
 *     System.out.println(o.getName() + " " + o.getDate() + " " + o.getTime());
 *     for (MenuItem m : o.getItems()) {
 *         System.out.println("  " + m.getName() + " $" + m.getPrice());
 *     }
 * }
 * }</pre>
 */
public class Order {
    private final int id;
    private final LocalDate date;
    private final LocalTime time;
    private final double total;
    private final Map<MenuItem, Integer> itemQuantities = new LinkedHashMap<>();

    /**
     * Creates an order with the given identity and totals.
     *
     * @param id the database identifier of the order
     * @param date the date the order was placed
     * @param time the time the order was placed
     * @param total the total price of the order
     */
    public Order(int id, LocalDate date, LocalTime time, double total) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.total = total;
    }

    /**
     * Adds an item to the order, summing the quantities when the same item is
     * added more than once. Used when loading an order's items from the
     * database.
     *
     * @param item the item to add
     * @param quantity the quantity of the item in the order
     */
    public void addItem(MenuItem item, int quantity) {
        itemQuantities.merge(item, quantity, Integer::sum);
    }

    /**
     * Returns the database identifier of the order.
     *
     * @return the order identifier
     */
    public int getId() { return id; }

    /**
     * Returns a display name of the form {@code Order #12}.
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
