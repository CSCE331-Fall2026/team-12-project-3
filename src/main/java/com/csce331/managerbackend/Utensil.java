package com.csce331.managerbackend;

/**
 * A utensil tracked in the inventory, with a current quantity on hand and a
 * threshold at or below which it should be restocked. Instances are created
 * through the {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class Utensil {
    private final String name;
    private int quantity;
    private int restockThreshold;

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
     * Builder for constructing {@link Utensil} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        private final String name;
        private int quantity;
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
