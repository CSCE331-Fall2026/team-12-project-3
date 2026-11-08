package com.csce331.requests;

/**
 * Request body for restocking an ingredient or a utensil.
 *
 * @param name the name of the ingredient or utensil
 * @param quantity the amount to restock
 * @param isUtensil whether the name refers to a utensil rather than an
 *                  ingredient
 */
public record RestockRequest(String name, int quantity, boolean isUtensil) {
    /**
     * Creates the request, rejecting a missing name and a non-positive quantity.
     *
     * @param name the name of the ingredient or utensil
     * @param quantity the amount to restock
     * @param isUtensil whether the name refers to a utensil rather than an
     *                  ingredient
     * @throws IllegalArgumentException if the name is null or empty, or the
     *         quantity is not positive
     */
    public RestockRequest {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }
}
