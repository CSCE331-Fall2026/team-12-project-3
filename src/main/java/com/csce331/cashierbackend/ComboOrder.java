package com.csce331.cashierbackend;

import java.util.List;

/**
 * Represents a combo order (such as a bowl or plate) that bundles multiple menu items.
 * Implements the {@link Order} interface to define combo pricing and item contents.
 */
public class ComboOrder implements Order {

    /** The name of the combo order. */
    private String name;

    /** The quantity of this combo ordered. */
    private int quantity;

    /** The override fixed price for the combo order. */
    private double price;

    /** The list of individual menu items included in this combo. */
    private List<MenuItem> items;

    /**
     * Constructs a new ComboOrder instance.
     *
     * @param name     the name of the combo
     * @param quantity the number of combo orders
     * @param price    the total fixed price for the combo
     * @param items    the menu items included in the combo
     */
    public ComboOrder(String name, int quantity, double price, List<MenuItem> items) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.items = items;
    }

    /**
     * Retrieves the name of the combo order.
     *
     * @return the combo name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retrieves the quantity of the combo ordered.
     *
     * @return the combo quantity
     */
    @Override
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the override price for the combo order.
     *
     * @return the combo price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retrieves the list of menu items included in this combo.
     *
     * @return a list of {@link MenuItem} objects
     */
    public List<MenuItem> getItems() {
        return items;
    }
}