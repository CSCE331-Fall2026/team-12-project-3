package com.csce331.managerbackend;

import java.time.LocalDate;

/**
 * A single restock batch of an ingredient, tracking how much was received and
 * when. Instances are created through the {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class InventoryItem {
    /** The ingredient this batch belongs to. */
    private final Ingredient ingredient;
    /** The date this batch was restocked. */
    private final LocalDate restockDate;
    /** The quantity remaining in this batch. */
    private int quantity;

    /**
     * Creates an inventory item from the given builder.
     *
     * @param builder the builder holding the batch's configuration
     */
    private InventoryItem(Builder builder) {
        this.ingredient = builder.ingredient;
        this.restockDate = builder.restockDate;
        this.quantity = builder.quantity;
    }

    /**
     * Returns the ingredient this item is a batch of.
     *
     * @return the associated ingredient
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Returns the date this batch was restocked.
     *
     * @return the restock date
     */
    public LocalDate getRestockDate() {
        return restockDate;
    }

    /**
     * Returns the current quantity remaining in this batch.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity remaining in this batch.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Increases the quantity of this batch by the given amount.
     *
     * @param quantity the amount to add; must not be negative
     * @throws IllegalArgumentException if {@code quantity} is negative
     */
    public void updateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity += quantity;
    }

    /**
     * Returns whether this batch has passed its expiration date, based on the
     * restock date and the ingredient's lifespan.
     *
     * @return {@code true} if the batch is expired, {@code false} otherwise
     */
    public boolean isExpired() {
        LocalDate expirationDate = restockDate.plusDays(ingredient.getLifespan());
        return LocalDate.now().isAfter(expirationDate);
    }

    /**
     * Builder for constructing {@link InventoryItem} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        /** The ingredient for the batch being built. */
        private final Ingredient ingredient;
        /** The restock date for the batch being built. */
        private LocalDate restockDate;
        /** The initial quantity for the batch being built. */
        private int quantity;

        /**
         * Creates a builder for an inventory item of the given ingredient. The
         * restock date defaults to now and the quantity defaults to zero.
         *
         * @param ingredient the ingredient this item is a batch of
         */
        public Builder(Ingredient ingredient) {
            this.ingredient = ingredient;
            this.restockDate = LocalDate.now();
            this.quantity = 0;
        }

        /**
         * Sets the date this batch was restocked.
         *
         * @param restockDate the restock date
         * @return this builder
         */
        public Builder restockDate(LocalDate restockDate) {
            this.restockDate = restockDate;
            return this;
        }

        /**
         * Sets the initial quantity of this batch.
         *
         * @param quantity the quantity
         * @return this builder
         */
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Builds the configured {@link InventoryItem}.
         *
         * @return a new inventory item
         * @throws IllegalStateException if the ingredient is null, the restock
         *         date is null or in the future, or the quantity is negative
         */
        public InventoryItem build() {
            if (ingredient == null) {
                throw new IllegalStateException("Ingredient cannot be null");
            }
            if (restockDate == null) {
                throw new IllegalStateException("Restock date cannot be null");
            }
            if (restockDate.isAfter(LocalDate.now())) {
                throw new IllegalStateException("Restock date cannot be in the future");
            }

            if (quantity < 0) {
                throw new IllegalStateException("Quantity cannot be negative");
            }

            return new InventoryItem(this);
        }
    }
}
