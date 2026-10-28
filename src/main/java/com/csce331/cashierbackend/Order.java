package com.csce331.cashierbackend;

/**
 * Interface representing a customer order.
 * Defines contract methods for retrieving order identity and item count.
 */
public interface Order {

    /**
     * Retrieves the name of the order item or item group.
     *
     * @return the name string
     */
    String getName();

    /**
     * Retrieves the quantity of items included in this order.
     *
     * @return the item quantity
     */
    int getQuantity();

    
}