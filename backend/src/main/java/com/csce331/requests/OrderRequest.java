package com.csce331.requests;

import java.util.Map;

import com.csce331.models.MenuItem;
import com.csce331.models.PaymentMethod;

/**
 * Request body for placing a customer order.
 *
 * @param items the ordered menu items and their quantities
 * @param method the payment method used at checkout
 * @param price the total price of the order
 */
public record OrderRequest(Map<MenuItem, Integer> items, PaymentMethod method, double price) {
    /**
     * Creates the request, rejecting an empty order, a missing payment method,
     * and a negative price.
     *
     * @param items the ordered menu items and their quantities
     * @param method the payment method used at checkout
     * @param price the total price of the order
     * @throws IllegalArgumentException if {@code items} is null or empty, the
     *         payment method is null, or the price is negative
     */
    public OrderRequest {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be null or empty");
        }
        if (method == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
    }
}
