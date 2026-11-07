package com.csce331.requests;

public record RestockRequest(String name, int quantity, boolean isUtensil) {
    public RestockRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
