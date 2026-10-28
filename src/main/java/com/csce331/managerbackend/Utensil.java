package com.csce331.managerbackend;

/**
 * A utensil tracked in the inventory, with a current quantity on hand and a
 * threshold at or below which it should be restocked. Built using a
 * {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class Utensil implements Restockable {
    /** The name of the utensil. */
    private final String name;
    /** The current quantity on hand. */
    private int quantity;
    /** The quantity at or below which the utensil should be restocked. */
    private int restockThreshold;

    /**
     * Creates a utensil from the given builder.
     *
     * @param builder the builder holding the utensil's configuration
     */
    private Utensil(Builder builder) {
        this.name = builder.name;
        this.quantity = builder.quantity;
        this.restockThreshold = builder.restockThreshold;
    }

    /**
     * Returns the name of the utensil.
     *
     * @return the utensil's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the current quantity on hand.
     *
     * @return the utensil's quantity
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * Returns the quantity at or below which the utensil should be restocked.
     *
     * @return the utensil's restock threshold
     */
    public int getRestockThreshold() {
        return this.restockThreshold;
    }

    /**
     * Sets the current quantity on hand.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    /**
     * Sets the quantity at or below which the utensil should be restocked.
     *
     * @param threshold the new restock threshold
     * @return the SQL query to update the utensil's restock threshold in the database
     * @throws IllegalArgumentException if the threshold is not positive
     */
    public String setRestockThreshold(int threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException("Utensil restock threshold must be positive");
        }
        this.restockThreshold = threshold;
        return "UPDATE utensils " +
            "SET restock_threshold = " + threshold + " " +
            "WHERE name = '" + this.name + "';";
    }
    
    /**
     * Increases the current quantity on hand by the given amount.
     *
     * @param quantity the amount to add to the current quantity
     * @return the SQL query to update the utensil's quantity in the database
     * @throws IllegalArgumentException if the quantity is not positive
     */
    public String restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }

        this.quantity += quantity;
        return "UPDATE utensils " +
            "SET current_amount = " + this.quantity + " " +
            "WHERE name = '" + this.name + "';";
    }

    /**
     * Returns whether the current quantity has reached or fallen below the
     * restock threshold.
     *
     * @return {@code true} if the utensil needs restocking, {@code false}
     *         otherwise
     */
    public boolean isRestockNeeded() {
        return this.quantity <= this.restockThreshold;
    }

    /**
     * Builder for constructing {@link Utensil} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        /** The name of the utensil being built. */
        private final String name;
        /** The initial quantity for the utensil being built. */
        private int quantity;
        /** The restock threshold for the utensil being built. */
        private int restockThreshold;

        /**
         * Starts a builder for a utensil with the given name; the quantity
         * defaults to zero and the restock threshold to one.
         *
         * @param name the name of the utensil
         */
        public Builder(String name) {
            this.name = name;
            this.quantity = 0;
            this.restockThreshold = 1;
        }

        /**
         * Sets the current quantity on hand.
         *
         * @param quantity the quantity of the utensil
         * @return this builder
         */
        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        /**
         * Sets the quantity at or below which the utensil should be restocked.
         *
         * @param restockThreshold the restock threshold
         * @return this builder
         */
        public Builder restockThreshold(int restockThreshold) {
            this.restockThreshold = restockThreshold;
            return this;
        }

        /**
         * Builds the configured {@link Utensil}.
         *
         * @return the constructed utensil
         * @throws IllegalArgumentException if the name is empty, the restock
         *         threshold is not positive, or the quantity is negative
         */
        public Utensil build() {
            if (this.name == null || this.name.isEmpty()) {
                throw new IllegalArgumentException("Utensil name must be specified");
            }

            if (this.restockThreshold <= 0) {
                throw new IllegalArgumentException("Utensil restock threshold must be positive");
            }

            if (this.quantity < 0) {
                throw new IllegalArgumentException("Utensil quantity cannot be negative");
            }

            return new Utensil(this);
        }
    }
}
