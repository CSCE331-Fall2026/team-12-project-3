package com.csce331.requests;

import java.util.Map;

import com.csce331.models.MenuItem;
import com.csce331.models.PaymentMethod;

public record OrderRequest(Map<MenuItem, Integer> items, PaymentMethod method, double price) {
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
