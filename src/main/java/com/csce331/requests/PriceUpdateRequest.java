package com.csce331.requests;

import com.csce331.models.MenuItemSize;

/**
 * Request body identifying a menu item and the price to give it.
 *
 * @param name the name of the menu item
 * @param size the portion size of the menu item
 * @param price the new price
 */
public record PriceUpdateRequest(String name, MenuItemSize size, double price) {
    /**
     * Creates the request, rejecting a missing name or size and a negative
     * price.
     *
     * @param name the name of the menu item
     * @param size the portion size of the menu item
     * @param price the new price
     * @throws IllegalArgumentException if the name is null or empty, the size is
     *         null, or the price is negative
     */
    public PriceUpdateRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (size == null) {
            throw new IllegalArgumentException("Size cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
}
