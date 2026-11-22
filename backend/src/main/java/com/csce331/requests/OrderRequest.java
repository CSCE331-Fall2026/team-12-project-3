package com.csce331.requests;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.javalin.openapi.OpenApiRequired;

import com.csce331.models.PaymentMethod;

/**
 * Request body for placing a customer order.
 *
 * <p>Jackson deserializes the request from a JSON object such as:
 * <pre>{@code
 * {
 *   "items": { "1": 2, "3": 1 },
 *   "method": "CASH",
 *   "price": 12.5
 * }
 * }</pre>
 * The keys of {@code items} are menu item IDs and its values are the quantities
 * ordered, so the example orders two of menu item 1 and one of menu item 3.
 * JSON object keys are strings, but Jackson converts them to the integer IDs
 * this record stores. The {@code method} must name one of the
 * {@link PaymentMethod} constants.
 *
 * <p>A request with missing or empty {@code items}, a missing {@code method},
 * or a missing or negative {@code price} is rejected.
 *
 * @param items the ordered menu item IDs and their quantities
 * @param method the payment method used at checkout
 * @param price the total price of the order
 */
public record OrderRequest(
        @JsonProperty(required = true) @OpenApiRequired Map<Integer, Integer> items,
        @JsonProperty(required = true) @OpenApiRequired PaymentMethod method,
        @JsonProperty(required = true) @OpenApiRequired double price) {
    /**
     * Creates the request, rejecting an empty order, a missing payment method,
     * and a negative price.
     *
     * @param items the ordered menu item IDs and their quantities
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
