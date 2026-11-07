package com.csce331.requests;

import com.csce331.models.MenuItemSize;

public record PriceUpdateRequest(String name, MenuItemSize size, double price) {
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
