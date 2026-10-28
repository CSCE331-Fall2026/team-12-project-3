package com.csce331.cashierbackend;

/**
 * Represents a standard, single-item menu order (such as an entree, side, or appetizer).
 * Implements the {@link Order} interface to provide core order attributes.
 */
public class BasicOrder implements Order {

    /** The display name of the ordered item. */
    private String name;

    /** The number of units ordered for this item. */
    private int quantity;

    /** The portion size for this basic order (SMALL, MEDIUM, or LARGE). */
    private OrderSize size;

    /**
     * Constructs a new BasicOrder instance with specified details.
     *
     * @param name     the name of the ordered item
     * @param quantity the quantity of items ordered
     * @param size     the size selection for the order
     */
    public BasicOrder(String name, int quantity, OrderSize size) {
        this.name = name;
        this.quantity = quantity;
        this.size = size;
    }

    /**
     * Retrieves the name of the ordered item.
     *
     * @return the item name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retrieves the quantity of the ordered item.
     *
     * @return the item quantity
     */
    @Override
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the size specification for this basic order.
     *
     * @return the selected {@link OrderSize}
     */
    public OrderSize getSize() {
        return size;
    }
}