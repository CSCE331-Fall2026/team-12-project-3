package com.csce331.managerbackend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import java.time.LocalDate;

/**
 * An ingredient tracked in the inventory, described by its name, unit of
 * measurement, storage location, shelf life in days, and the quantity on hand at
 * or below which it should be restocked. These values are fixed once the
 * ingredient is built. Instances are created through the {@link Builder}.
 *
 * @author Kedar Panchal
 */
public class Ingredient {
    /** The name of the ingredient. */
    private final String name;
    /** The unit of measurement for the ingredient. */
    private final Unit unit;
    /** The storage location of the ingredient. */
    private final InventoryLocation location;
    /** The shelf life of the ingredient in days. */
    private final int lifespan;
    /** The quantity at or below which the ingredient should be restocked. */
    private int restockThreshold;

    /**
     * Creates an ingredient from the given builder.
     *
     * @param builder the builder holding the ingredient's configuration
     */
    private Ingredient(Builder builder) {
        this.name = builder.name;
        this.unit = builder.unit;
        this.location = builder.location;
        this.lifespan = builder.lifespan;
        this.restockThreshold = builder.restockThreshold;
    }

    /**
     * Returns the name of the ingredient.
     *
     * @return the ingredient name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the unit of measurement for the ingredient.
     *
     * @return the unit
     */
    public Unit getUnit() {
        return this.unit;
    }

    /**
     * Returns the storage location of the ingredient.
     *
     * @return the inventory location
     */
    public InventoryLocation getLocation() {
        return this.location;
    }

    /**
     * Returns the shelf life of the ingredient in days.
     *
     * @return the lifespan in days
     */
    public int getLifespan() {
        return this.lifespan;
    }

    /**
     * Returns the quantity at or below which the ingredient should be
     * restocked.
     *
     * @return the restock threshold
     */
    public int getRestockThreshold() {
        return this.restockThreshold;
    }

    /**
     * Builder for constructing {@link Ingredient} instances.
     *
     * @author Kedar Panchal
     */
    public static class Builder {
        /** The name of the ingredient being built. */
        private final String name;
        /** The unit of measurement for the ingredient being built. */
        private final Unit unit;
        /** The storage location of the ingredient being built. */
        private final InventoryLocation location;
        /** The shelf life in days for the ingredient being built. */
        private int lifespan;
        /** The restock threshold for the ingredient being built. */
        private int restockThreshold;

        /**
         * Creates a builder for an ingredient with the given identity. The
         * lifespan defaults to zero and the restock threshold defaults to one.
         *
         * @param name the ingredient name
         * @param unit the unit of measurement
         * @param location the storage location
         */
        public Builder(String name, Unit unit, InventoryLocation location) {
            this.name = name;
            this.unit = unit;
            this.location = location;
            this.lifespan = 0;
            this.restockThreshold = 1;
        }

        /**
         * Sets the shelf life of the ingredient in days.
         *
         * @param lifespan the lifespan in days, which must not be negative
         * @return this builder
         */
        public Builder lifespan(int lifespan) {
            this.lifespan = lifespan;
            return this;
        }

        /**
         * Sets the quantity on hand at or below which the ingredient should be
         * restocked.
         *
         * @param restockThreshold the restock threshold, which must be positive
         * @return this builder
         */
        public Builder restockThreshold(int restockThreshold) {
            this.restockThreshold = restockThreshold;
            return this;
        }

        /**
         * Builds the configured {@link Ingredient}.
         *
         * @return a new ingredient
         * @throws IllegalArgumentException if the name is null or empty, the unit
         *         or location is null, the lifespan is negative, or the restock
         *         threshold is not positive
         */
        public Ingredient build() {
            if (this.name == null || this.name.isEmpty()) {
                throw new IllegalArgumentException("Ingredient name must be specified");
            }

            if (this.unit == null) {
                throw new IllegalArgumentException("Ingredient unit must be specified");
            }

            if (this.location == null) {
                throw new IllegalArgumentException("Ingredient location must be specified");
            }

            if (this.lifespan < 0) {
                throw new IllegalArgumentException("Ingredient lifespan cannot be negative");
            }

            if (this.restockThreshold <= 0) {
                throw new IllegalArgumentException("Ingredient restock threshold must be positive");
            }

            return new Ingredient(this);
        }
    }
}
